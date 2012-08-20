structure Directives : DIRECTIVES =
struct

datatype directive =
	 AS of string
       | AT of string
       | BS of Operands.expr
       | DA of Operands.expr list
       | EQ of Operands.expr
       | HS of int list   
       | IN of string
       | OR of Operands.expr
       | TF of string
	       
fun parseStringArg substr =
    case Substring.string (Substring.dropl Char.isSpace substr) of
	""  => raise Operands.BadAddressError
      | str => str
	       
fun parseDelimArg substr =
    case Substring.getc (Substring.dropl Char.isSpace substr) of
	SOME (c,substr') =>
	let 
	    val (str,rest) = Substring.splitl (fn c' => not (c'=c)) substr'
        in
	    case Substring.getc rest of
		SOME (c,_) => Substring.string str
              | _          => raise Operands.BadAddressError
        end
      | _ => raise Operands.BadAddressError
		   
fun parseExprList substr =
    case Operands.parseList Operands.parseExpr substr of
        SOME ([],_)  => raise Operands.BadAddressError
      | SOME (lst,_) => lst
      | NONE         => raise Operands.BadAddressError
			      
fun parseHexString substr =
    let
	fun getHexNum substr =
	    let
		val (digits,_) = Substring.splitAt(substr,2)
		    handle Subscript => raise Operands.BadAddressError
            in
		case StringCvt.scanString(Int.scan StringCvt.HEX) 
					 (Substring.string digits) of
		    SOME n => n
	          | NONE   => raise Operands.BadAddressError
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
	    []      => raise Operands.BadAddressError
	  | results => results
    end
    
fun parse substr =
    let
	val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 
    in
	case (Substring.translate (Char.toString o Char.toUpper) mem) of
	    ".AS" => SOME (AS (parseDelimArg rest))
	  | ".AT" => SOME (AT (parseDelimArg rest))
	  | ".BS" => SOME (BS (Operands.parseExprArg rest))
	  | ".DA" => SOME (DA (parseExprList rest))
          | ".EQ" => SOME (EQ (Operands.parseExprArg rest))
	  | ".HS" => SOME (HS (parseHexString rest))
          | ".IN" => SOME (IN (parseStringArg rest))
          | ".OR" => SOME (OR (Operands.parseExprArg rest))
	  | ".TF" => SOME (TF (parseStringArg rest))
          | _ => NONE
    end
    
end
