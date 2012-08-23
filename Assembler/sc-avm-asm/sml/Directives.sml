structure Directives : DIRECTIVES =
struct

open Error

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
       | Ignored
	       
fun parseStringArg substr =
    case Substring.string (Substring.dropl Char.isSpace substr) of
	""  => raise BadAddress
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
    case Operands.parseList Operands.parseExpr substr of
        SOME ([],_)  => raise BadAddress
      | SOME (lst,_) => lst
      | NONE         => raise BadAddress
			      
fun parse substr =
    let
	val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 
	val dir = Substring.translate (Char.toString o Char.toUpper) mem
    in
	case dir of
	    ".AS" => SOME (AS (parseDelimArg rest))
	  | ".AT" => SOME (AT (parseDelimArg rest))
	  | ".BS" => SOME (BS (Operands.parseExprArg rest))
	  | ".DA" => SOME (DA (parseExprList rest))
          | ".EQ" => SOME (EQ (Operands.parseExprArg rest))
	  | ".HS" => SOME (HS (Operands.parseHexString rest))
          | ".IN" => SOME (IN (parseStringArg rest))
          | ".OR" => SOME (OR (Operands.parseExprArg rest))
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
