structure Instruction : INSTRUCTION =
struct

open Error
     
datatype t =
	 Native of Native.t
       | Directive of Directive.t
       | AppleCore of AppleCore.t
		      
fun apply parser substr =
    parser (Substring.dropl Char.isSpace substr)
    
fun parse' substr = 
    case apply Native.parse substr of
	SOME inst => SOME (Native inst)
      | _  => (case apply AppleCore.parse substr of
		   SOME inst => SOME (AppleCore inst)
		 | _ => (case apply Directive.parse substr of
			     SOME inst => SOME (Directive inst)
			   | _ => NONE))
	      
fun parse substr =
    case parse' substr of
	SOME i => SOME i
      | _  =>
	let
	    val rest = Substring.dropl Char.isSpace substr
	in
	    if Substring.isEmpty rest
	    then NONE
	    else raise AssemblyError 
			(InvalidMnemonic 
			     (Substring.string 
				  (Substring.takel (not o Char.isSpace) rest)))
	end
	
fun includeIn inst (paths,file) =
    case inst of
	Directive d =>
	Directive.includeIn d (paths,file)
      | _ => file

fun pass1 (AppleCore inst) (label,source,map) = 
    AppleCore.pass1 inst (label,source,map)
  | pass1 (Directive inst) (label,source,map) = 
    Directive.pass1 inst (label,source,map)
  | pass1 (Native inst) (label,source,map) = 
    Native.pass1 inst (label,source,map)

end

  
