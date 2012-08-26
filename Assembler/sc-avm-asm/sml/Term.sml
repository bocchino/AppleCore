structure Term : TERM =
struct

open Error
	    
datatype t =
	 Number of int
       | Label of Labels.label
       | Character of char
       | Star
	 
fun parse substr =
    case Numbers.parse substr of
	SOME (n,substr') => SOME (Number (Numbers.normalize 65536 n),substr')
      |  _ => 
	 (case Labels.parse substr of
	      SOME (l,substr') => SOME (Label l,substr')
	    | _ => 
	      (case Substring.getc substr of
	           SOME(#"'",substr') =>
		   (case Substring.getc substr' of
		       SOME (c,substr'') => SOME (Character c,substr'')
		     | _                 => raise BadAddress)
		 | SOME(#"*",substr') => SOME (Star,substr')
		 | _ => NONE))
	      
end
