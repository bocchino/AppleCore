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

fun parseGlobalLabel (c,substr,expr) =
    if Char.isAlpha c then
	let
	    val (label,substr) = Substring.splitl isLabelChar substr
	    val label = Substring.string label
	in
	    (if expr then () else globalLabel := SOME label;
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

fun parse (substr,expr) =
    case Substring.getc substr of
	NONE              => NONE
      | SOME(#".",substr) => parseLocalLabel substr
      | SOME(c,substr')    => parseGlobalLabel (c,substr,expr)

fun parseMain substr = parse (substr,false)
fun parseExpr substr = parse (substr,true)

structure Map = SplayMapFn(struct
			   type ord_key = string
			   val compare = String.compare
			   end)

type source = {sourceLine:File.line,
	       address:int}
type map = source Map.map

val fresh = Map.empty

fun labelAsString (Global str) = str
  | labelAsString (Local (str,n)) = str ^ "$" ^ (Int.toString n) 

fun lookup (map:map,label:t) =
    case Map.find (map,labelAsString label) of
	SOME {address=a,...} => SOME a
      | NONE => NONE

fun update (map,label,source) =
    Map.insert (map,labelAsString label,source)
					      
fun add (map,label,source) =
    case Map.find (map,labelAsString label) of
	SOME {sourceLine,address} => 
	raise AssemblyError (RedefinedLabel {file=(File.fileName sourceLine),
					     lineNum=(File.lineNumber sourceLine)})
      | NONE => update (map,label,source)
  
end

