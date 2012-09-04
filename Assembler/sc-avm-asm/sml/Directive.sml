structure Directive : INSTRUCTION =
struct

open Error

datatype DAExpr =
	 All of Expression.t
       | Low of Expression.t
       | High of Expression.t

datatype t =
	 AS of string
       | AT of string
       | BS of Expression.t
       | DA of DAExpr list
       | EQ of Expression.t
       | HS of int list   
       | IN of string
       | OR of Expression.t
       | TF of string
       | Ignored

fun parseStringArg substr =
    case Substring.string (Substring.dropl Char.isSpace substr) of
	""  => raise AssemblyError BadAddress
      | str => str
	    
fun parseFileArg substr =
    case Substring.string (Substring.takel (fn c => not (c = #",")) 
					   (Substring.dropl Char.isSpace substr)) of
	"" => raise AssemblyError BadAddress
      | str => str
   
fun parseDelimArg substr =
    case Substring.getc (Substring.dropl Char.isSpace substr) of
	SOME (c,substr') =>
	let 
	    val (str,rest) = Substring.splitl (fn c' => not (c'=c)) substr'
        in
	    case Substring.getc rest of
		SOME (c,_) => Substring.string str
              | _          => raise AssemblyError BadAddress
        end
      | _ => raise AssemblyError BadAddress

fun parseDAExpr substr =
    case Substring.getc substr of
	SOME(#"#",substr) => (case Expression.parse substr of
				  SOME (expr,substr) => SOME (Low expr,substr)
				| NONE => raise AssemblyError BadAddress)
      | SOME(#"/",substr) => (case Expression.parse substr of
				  SOME (expr,substr) => SOME (High expr,substr)
				| NONE => raise AssemblyError BadAddress)
      | _ => (case Expression.parse substr of
		  SOME (expr,substr) => SOME (All expr,substr)
		| NONE => raise AssemblyError BadAddress)

fun parseDAExprList substr =
    case Parsing.parseList parseDAExpr substr of
	SOME (result as hd::tl,_) => result
      | _ => raise AssemblyError BadAddress
			      
fun parseHexString substr =
    let
	fun getHexNum substr =
	    let
		val (digits,_) = Substring.splitAt(substr,2)
		    handle Subscript => raise AssemblyError BadAddress
            in
		case StringCvt.scanString(Int.scan StringCvt.HEX) 
					 (Substring.string digits) of
		    SOME n => n
	          | NONE   => raise AssemblyError BadAddress
            end
	fun parseHexString' results substr =
	    case Substring.first substr of
		NONE   => List.rev results
              | SOME c =>
		if Char.isSpace c then 
		    List.rev results
		else
		    let 
			val num = getHexNum substr
		    in
			parseHexString' (num :: results) (Substring.triml 2 substr)
		    end
    in
	case parseHexString' [] (Substring.dropl Char.isSpace substr) of
	    []      => raise AssemblyError BadAddress
	  | results => results
    end
    
fun parse substr =
    let
	val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 
	val dir = Substring.translate (Char.toString o Char.toUpper) mem
    in
	case dir of
	    ".AS" => SOME (AS (parseDelimArg rest))
	  | ".AT" => SOME (AT (parseDelimArg rest))
	  | ".BS" => SOME (BS (Expression.parseArg rest))
	  | ".DA" => SOME (DA (parseDAExprList rest))
          | ".EQ" => SOME (EQ (Expression.parseArg rest))
	  | ".HS" => SOME (HS (parseHexString rest))
          | ".IN" => SOME (IN (parseFileArg rest))
          | ".OR" => SOME (OR (Expression.parseArg rest))
	  | ".TF" => SOME (TF (parseFileArg rest))
	  | ".TA" => SOME Ignored
	  | ".TI" => SOME Ignored
	  | ".LIST" => SOME Ignored
	  | ".PG" => SOME Ignored
	  | ".EN" => raise AssemblyError UnsupportedDirective
	  | ".DO" => raise AssemblyError UnsupportedDirective
	  | ".ELSE" => raise AssemblyError UnsupportedDirective
	  | ".FIN" => raise AssemblyError UnsupportedDirective
	  | ".MA" => raise AssemblyError UnsupportedDirective
	  | ".EM" => raise AssemblyError UnsupportedDirective
	  | ".US" => raise AssemblyError UnsupportedDirective
          | _ => NONE
    end

fun includeIn inst file =
    case inst of
	IN name =>
	File.includeIn file name
      | _ => file

fun pass1 (label,inst) (source as {sourceLine,address},map) =
    let
	fun eval expr =
	    Expression.evalAsAddr (address,map) expr
	fun sizeOf [] = 0
	  | sizeOf ((All _) :: tail) = 2 + (sizeOf tail)
	  | sizeOf (_ :: tail) = 1 + (sizeOf tail)
    in
	case (label,inst) of 
	    (_,AS str) => (inst,address + (size str),map)
	  | (_,AT str) => (inst,address + (size str),map)
	  | (_,BS expr) => (inst,address + (eval expr),map)
	  | (_,DA DAexprs) => (inst,address + (sizeOf DAexprs),map)
	  | (NONE,EQ _) => raise AssemblyError NoLabel
	  | (SOME label,EQ expr) => (inst,address,
				     Label.update (map,label,{sourceLine=sourceLine,
							      address=eval expr}))
	  | (_,HS args) => (inst,address + (List.length args),map)
	  | (_,OR expr) => (inst,eval expr,map)
	  | _ => (inst,address,map)
    end

fun list (listFn,line,inst,addr) =
    let
	fun listWithAddr () =
	    listFn (Printing.formatLine (SOME addr,[],File.data line))
	fun listWithoutAddr () =
	    listFn (Printing.formatLine (NONE,[],File.data line))
    in
	case inst of
	    EQ expr => listWithoutAddr ()
	  | OR _ => listWithoutAddr ()
	  | IN _ => listWithoutAddr ()
	  | TF _ => listWithoutAddr ()
	  | Ignored => listWithoutAddr() 
	  | _ => listWithAddr ()
    end

fun instBytes (inst,addr,map) = 
    let
	fun bytes expr =
	    Numbers.bytes (Expression.evalAsAddr (addr,map) expr)
	fun lowByte expr =
	    Numbers.lowByte (Expression.evalAsAddr (addr,map) expr)
	fun highByte expr =
	    Numbers.highByte (Expression.evalAsAddr (addr,map) expr)
	fun ASBytes str =
	    List.map Char.ord (String.explode str)
	fun ATBytes str =
	    let
		val bytes = ASBytes str
	    in
		case List.rev bytes of
		    head :: tail => List.rev ( (head + 0x80) :: tail )
		  | _ => bytes 
	    end
	fun BSBytes expr =
	    let
		val length = Expression.evalAsAddr (addr,map) expr
		fun  BSBytes bytes 0 = bytes
		   | BSBytes bytes n = BSBytes (0 :: bytes) (n-1)
	    in
		BSBytes [] length
	    end
	fun DABytes exprs =
	    let
		fun DAMap (All expr) = bytes expr
		  | DAMap (Low expr) = [lowByte expr]
		  | DAMap (High expr) = [highByte expr]
	    in
		List.foldl op@ [] (List.map DAMap exprs)
	    end
    in
	case inst of
	    AS str => ASBytes str
	  | AT str => ATBytes str
	  | BS expr => BSBytes expr
	  | DA exprs => DABytes exprs
	  | HS bytes => bytes
	  | _ => []
    end


fun pass2 (output,sourceLine,inst,addr,map) =
    let
	val bytes = instBytes (inst,addr,map)
	val output = case inst of
			 TF str => Output.TF (output,str,addr)
		       | _ => Output.addBytes (output,bytes)
	val addr = case bytes of
		       [] => NONE
		     | _ => SOME addr
    in
	(output,
	 Printing.formatLine (addr,bytes,File.data sourceLine))
    end
end
