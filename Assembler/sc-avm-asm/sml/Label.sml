structure Label : LABEL =
struct

open Error

datatype t =
	 (* A global label, such as FOO *)
	 Global of string
       (* A local label, such as .12 *)
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

structure GlobalMap = SplayMapFn(struct
				 type ord_key = string
				 val compare = String.compare
				 end)

structure LocalMap = SplayMapFn(struct
				type ord_key = int
				val compare = Int.compare
				end)

type source = {file:string,line:int,address:int}
type globalMap = source GlobalMap.map;
type localMap = source LocalMap.map;
type map = {lm:localMap,gm:globalMap}

exception RedefinedLabel of t * source

val fresh = {lm=LocalMap.empty,gm=GlobalMap.empty}

fun lookup' (m:map,l:t) =
    case (m,l) of
	({lm,gm},Global str) => GlobalMap.find (gm,str)
      | ({lm,gm},Local n)  => LocalMap.find (lm,n)

fun lookup (m:map,l:t) =
    case lookup' (m,l) of
	SOME {address=a,...} => SOME a
      | NONE => NONE

fun add (m:map,l:t,s:source) =
    case lookup' (m,l) of
	SOME s' => raise RedefinedLabel(l,s')
      | NONE => (case (m,l) of
	({lm,gm},Global str) => 
	{lm=LocalMap.empty,gm=GlobalMap.insert (gm,str,s)}
      | ({lm,gm},Local n) =>  
	{lm=(LocalMap.insert (lm,n,s)),gm=gm})
    
end

