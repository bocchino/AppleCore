structure Label : LABEL =
struct

open Error

datatype t =
	 (* A global label, such as FOO *)
	 Global of string
         (* A local label, such as .12 *)
       | Local of string * int

(* Storage for last global label seen during parsing *)
val globalLabel = ref (NONE:string option)

fun parseDigits substr = 
    case Numbers.parseDigits substr StringCvt.DEC of 
	SOME (n,substr') => 
	((Numbers.normalize 100 n,substr') 
	 handle AssemblyError RangeError => 
		raise AssemblyError BadLabel)
      | _   => raise AssemblyError BadLabel
		     
fun isLabelChar c =
    Char.isAlphaNum c orelse c = #"."

fun parseGlobalLabel (c,substr) =
    if Char.isAlpha c then
	let
	    val (label,substr) = Substring.splitl isLabelChar substr
	    val label = Substring.string label
	in
	    (globalLabel := SOME label;
	     SOME (Global label,substr))
	end
    else NONE
			     
fun parseLocalLabel substr =
    let
	val (n,substr) = parseDigits substr
    in
	case !globalLabel of
	    SOME str => SOME (Local (str,n),substr)
	  | NONE => raise AssemblyError NoGlobalLabel
    end

fun parse substr =
    case Substring.getc substr of
	NONE              => NONE
      | SOME(#".",substr) => parseLocalLabel substr
      | SOME(c,substr')    => parseGlobalLabel (c,substr)

structure GlobalMap = SplayMapFn(struct
				 type ord_key = string
				 val compare = String.compare
				 end)

structure LocalMap = SplayMapFn(struct
				type ord_key = int
				val compare = Int.compare
				end)

type source = {sourceLine:File.line,
	       address:int}
type globalMap = source GlobalMap.map;
type localMap = source LocalMap.map;
type map = {localMap:localMap,globalMap:globalMap}

val fresh = {localMap=LocalMap.empty,globalMap=GlobalMap.empty}

fun lookup' (map,label) =
    case (map,label) of
	({localMap,globalMap},Global str) => GlobalMap.find (globalMap,str)
      | ({localMap,globalMap},Local (str,n))  => LocalMap.find (localMap,n)

fun lookup (map:map,label:t) =
    case lookup' (map,label) of
	SOME {address=a,...} => SOME a
      | NONE => NONE

fun update (map,label,source) =
    case (map,label) of
	({localMap,globalMap},Global str) => 
	{localMap=LocalMap.empty,globalMap=GlobalMap.insert (globalMap,str,source)}
      | ({localMap,globalMap},Local (str,n)) =>  
	{localMap=(LocalMap.insert (localMap,n,source)),globalMap=globalMap}
					      
fun add (map,label,source) =
    case lookup' (map,label) of
	SOME {sourceLine,address} => 
	raise AssemblyError (RedefinedLabel {file=(File.fileName sourceLine),
					     lineNum=(File.lineNumber sourceLine)})
      | NONE => update (map,label,source)
  
end

