structure Directive : INSTRUCTION =
struct

open Error

datatype t =
	 AS of string
       | AT of string
       | BS of Expression.t
       | DA of Expression.t list
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
      | str => str ^ ".avm"
   
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
		   
fun parseExprList substr =
    case Expression.parseList Expression.parse substr of
        SOME ([],_)  => raise AssemblyError BadAddress
      | SOME (lst,_) => lst
      | NONE         => raise AssemblyError BadAddress
			      
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
	  | ".DA" => SOME (DA (parseExprList rest))
          | ".EQ" => SOME (EQ (Expression.parseArg rest))
	  | ".HS" => SOME (HS (parseHexString rest))
          | ".IN" => SOME (IN (parseFileArg rest))
          | ".OR" => SOME (OR (Expression.parseArg rest))
	  | ".TF" => SOME (TF (parseStringArg rest))
	  | ".TA" => SOME Ignored
	  | ".TI" => SOME Ignored
	  | ".LIST" => SOME Ignored
	  | ".PG" => SOME Ignored
	  | ".EN" => raise AssemblyError (UnsupportedDirective dir)
	  | ".DO" => raise AssemblyError (UnsupportedDirective dir)
	  | ".ELSE" => raise AssemblyError (UnsupportedDirective dir)
	  | ".FIN" => raise AssemblyError (UnsupportedDirective dir)
	  | ".MA" => raise AssemblyError (UnsupportedDirective dir)
	  | ".EM" => raise AssemblyError (UnsupportedDirective dir)
	  | ".US" => raise AssemblyError (UnsupportedDirective dir)
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
    in
	case (label,inst) of 
	    (_,AS str) => (inst,address + (size str),map)
	  | (_,AT str) => (inst,address + (size str),map)
	  | (_,BS expr) => (inst,address + (eval expr),map)
	  | (_,DA exprs) => (inst,address + 2 * (List.length exprs),map)
	  | (NONE,EQ _) => raise AssemblyError NoLabel
	  | (SOME label,EQ expr) => (inst,address,
				     Label.update (map,label,{sourceLine=sourceLine,
							      address=eval expr}))
	  | (_,HS args) => (inst,address + (List.length args),map)
	  | (_,OR expr) => (inst,eval expr,map)
	  | _ => (inst,address,map)
    end

end
