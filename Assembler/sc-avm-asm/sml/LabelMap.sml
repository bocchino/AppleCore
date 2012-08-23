structure LabelMap : LABEL_MAP =
struct

open Error
open Labels

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

exception RedefinedLabel of label * source

val fresh = {lm=LocalMap.empty,gm=GlobalMap.empty}

fun lookup' (m:map,l:label) =
    case (m,l) of
	({lm,gm},Global str) => GlobalMap.find (gm,str)
      | ({lm,gm},Local n)  => LocalMap.find (lm,n)

fun lookup (m:map,l:label) =
    case lookup' (m,l) of
	SOME {address=a,...} => SOME a
      | NONE => NONE

fun add (m:map,l:label,s:source) =
    case lookup' (m,l) of
	SOME s' => raise RedefinedLabel(l,s')
      | NONE => (case (m,l) of
	({lm,gm},Global str) => 
	{lm=LocalMap.empty,gm=GlobalMap.insert (gm,str,s)}
      | ({lm,gm},Local n) =>  
	{lm=(LocalMap.insert (lm,n,s)),gm=gm})
    
end

