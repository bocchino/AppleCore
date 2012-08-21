structure Operands : OPERANDS =
struct

exception BadAddressError
	    
datatype term =
	 Number of int
       | Label of Labels.label
       | Character of char
       | Star
	 
datatype expr =
	 Term of term
       | Add of term * expr
       | Sub of term * expr
       | Mul of term * expr
       | Div of term * expr
		
fun parseHexString substr =
    let
	fun getHexNum substr =
	    let
		val (digits,_) = Substring.splitAt(substr,2)
		    handle Subscript => raise BadAddressError
            in
		case StringCvt.scanString(Int.scan StringCvt.HEX) 
					 (Substring.string digits) of
		    SOME n => n
	          | NONE   => raise BadAddressError
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
	    []      => raise BadAddressError
	  | results => results
    end
    
fun parseNumberArg substr =
    case Numbers.parse (Substring.dropl Char.isSpace substr) of
	SOME (n,_) => n
      | _          => raise BadAddressError
			    
fun parseTerm substr =
    case Numbers.parse substr of
	SOME (n,substr') => SOME (Number (Numbers.normalize 65536 n),substr')
      |  _ => (
         case Labels.parse substr of
	     SOME (l,substr') => SOME (Label l,substr')
	   | _ => (
	     case Substring.getc substr of
	         SOME(#"'",substr') => (
		 case Substring.getc substr' of
		     SOME (c,substr'') => SOME (Character c,substr'')
		   | _                 => raise BadAddressError
		 )
               | SOME(#"*",substr') => SOME (Star,substr')
	       | _ => NONE
             )
	 )
	      
fun parseExpr substr =
    let fun binop oper t substr =
	    case parseExpr substr of
		SOME (e,substr'') => SOME (oper(t,e),substr'')
              | _                 => raise BadAddressError
    in
	
	case parseTerm (Substring.dropl Char.isSpace substr) of
	    SOME (t,substr') => (
	    case Substring.getc (Substring.dropl Char.isSpace substr') of
		SOME (#"+",substr'') => binop Add t substr''
              | SOME (#"-",substr'') => binop Sub t substr''
              | SOME (#"*",substr'') => binop Mul t substr''
              | SOME (#"/",substr'') => binop Div t substr''
	      | _                    => SOME (Term t,substr')
	    )
          | _ => NONE
		 
    end
    
fun parseListRest parse results substr =
    case Substring.getc (Substring.dropl Char.isSpace substr) of
	SOME (#",",substr') => (
	case parse (Substring.dropl Char.isSpace substr') of
	    SOME (result, substr'') => parseListRest parse (result :: results) substr''
          | _ => raise BadAddressError
	)
      | _ => SOME (List.rev results,substr)
	     
fun parseExprArg substr =
    case parseExpr substr of
	SOME (e,_) => e
      | _          => raise BadAddressError
			    
fun parseList parse substr =
    case parse (Substring.dropl Char.isSpace substr) of
	SOME (result,substr') => parseListRest parse [result] substr'
      | _ => SOME ([],substr)

end
