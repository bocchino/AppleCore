structure Label : LABEL =
struct

open Error

datatype t =
	 Global of string
       | Local of int

fun parseDigits substr = 
    case Numbers.parseDigits substr StringCvt.DEC of 
	SOME (n,substr') => 
	((Numbers.normalize 100 n,substr') 
	 handle RangeError => raise BadLabel)
      | _   => raise BadLabel
		     
fun isLabelChar c =
    Char.isAlphaNum c orelse c = #"."
			     
fun parse substr =
    case Substring.getc substr of
	NONE               => NONE
      | SOME(#".",substr') => 
	(case parseDigits substr' of
	     (n,substr'') => SOME (Local n,substr''))
      | SOME(c,substr')    =>				
	if Char.isAlpha c then
	    case Substring.splitl isLabelChar substr of
		(label,substr'') =>
		SOME (Global (Substring.string label),substr'')
        else NONE

end

