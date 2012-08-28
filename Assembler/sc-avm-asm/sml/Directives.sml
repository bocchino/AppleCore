structure Directives : DIRECTIVES =
struct

open Error

datatype directive =
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
	""  => raise BadAddress
      | str => str
	    
fun parseFileArg substr =
    case Substring.string (Substring.takel (fn c => not (c = #",")) 
					   (Substring.dropl Char.isSpace substr)) of
	"" => raise BadAddress
      | str => str
   
fun parseDelimArg substr =
    case Substring.getc (Substring.dropl Char.isSpace substr) of
	SOME (c,substr') =>
	let 
	    val (str,rest) = Substring.splitl (fn c' => not (c'=c)) substr'
        in
	    case Substring.getc rest of
		SOME (c,_) => Substring.string str
              | _          => raise BadAddress
        end
      | _ => raise BadAddress
		   
fun parseExprList substr =
    case Expression.parseList Expression.parse substr of
        SOME ([],_)  => raise BadAddress
      | SOME (lst,_) => lst
      | NONE         => raise BadAddress
			      
fun parseHexString substr =
    let
	fun getHexNum substr =
	    let
		val (digits,_) = Substring.splitAt(substr,2)
		    handle Subscript => raise BadAddress
            in
		case StringCvt.scanString(Int.scan StringCvt.HEX) 
					 (Substring.string digits) of
		    SOME n => n
	          | NONE   => raise BadAddress
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
	    []      => raise BadAddress
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
	  | ".EN" => raise UnsupportedDirective dir
	  | ".DO" => raise UnsupportedDirective dir
	  | ".ELSE" => raise UnsupportedDirective dir
	  | ".FIN" => raise UnsupportedDirective dir
	  | ".MA" => raise UnsupportedDirective dir
	  | ".EM" => raise UnsupportedDirective dir
	  | ".US" => raise UnsupportedDirective dir
          | _ => NONE
    end
    
end
