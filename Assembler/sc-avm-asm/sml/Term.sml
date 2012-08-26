structure Term : TERM =
struct

open Char
open Error
open LabelMap
	    
datatype t =
	 Number of int
       | Label of Label.t
       | Character of char
       | Star

fun parse substr =
    case Numbers.parse substr of
	SOME (n,substr') => SOME (Number (Numbers.normalize 65536 n),substr')
      |  _ => 
	 (case Label.parse substr of
	      SOME (l,substr') => SOME (Label l,substr')
	    | _ => 
	      (case Substring.getc substr of
	           SOME(#"'",substr') =>
		   (case Substring.getc substr' of
		       SOME (c,substr'') => SOME (Character c,substr'')
		     | _                 => raise BadAddress)
		 | SOME(#"*",substr') => SOME (Star,substr')
		 | _ => NONE))

fun eval (labelMap:map,starAddr:int) term:t =
    case term of
	Number n => term
      | Label l => 
	(case lookup (labelMap,l) of
	     SOME n => Number n
	   | NONE   => term)
      | Character c => Number (ord c)
      | Star => Number starAddr
    
end
