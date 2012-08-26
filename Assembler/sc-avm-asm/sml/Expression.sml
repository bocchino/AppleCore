structure Expression : EXPRESSION =
struct

open Char
open Error
open Substring
	    
datatype t =
	 Term of Term.t
       | Add of Term.t * t
       | Sub of Term.t * t
       | Mul of Term.t * t
       | Div of Term.t * t
		
fun parse substr =
    let fun binop oper t substr =
	    case parse substr of
		SOME (e,substr'') => SOME (oper(t,e),substr'')
              | _                 => raise BadAddress
    in
	case Term.parse (dropl isSpace substr) of
	    SOME (t,substr') =>
	    (case getc (dropl isSpace substr') of
		SOME (#"+",substr'') => binop Add t substr''
              | SOME (#"-",substr'') => binop Sub t substr''
              | SOME (#"*",substr'') => binop Mul t substr''
              | SOME (#"/",substr'') => binop Div t substr''
	      | _                    => SOME (Term t,substr'))
          | _ => NONE
    end
    
fun parseListRest parse results substr =
    case getc (dropl isSpace substr) of
	SOME (#",",substr') =>
	(case parse (dropl isSpace substr') of
	    SOME (result, substr'') => 
	    parseListRest parse (result :: results) substr''
          | _ => raise BadAddress)
      | _ => SOME (List.rev results,substr)
	     
fun parseArg substr =
    case parse substr of
	SOME (e,_) => e
      | _          => raise BadAddress
			    
fun parseList parse substr =
    case parse (dropl isSpace substr) of
	SOME (result,substr') => parseListRest parse [result] substr'
      | _ => SOME ([],substr)

end
