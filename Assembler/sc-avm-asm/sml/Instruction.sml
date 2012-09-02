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
	    else raise AssemblyError InvalidMnemonic
	end
	
fun includeIn inst file =
    case inst of
	Directive d => Directive.includeIn d file
      | _ => file

fun pass1 (label,inst) (source,map) = 
    let
	fun apply constr pass1 inst =
	    let 
		val (inst,addr,map) = pass1 (label,inst) (source,map)
	    in
		(constr inst,addr,map)
	    end
    in
	case inst of
	    AppleCore inst => apply AppleCore AppleCore.pass1 inst
	  | Directive inst => apply Directive Directive.pass1 inst
	  | Native inst => apply Native Native.pass1 inst
    end

fun pass2 (sourceLine,inst,addr,map) = 
    case inst of
	Native inst => Native.pass2 (sourceLine,inst,addr,map)
      | Directive inst => Directive.pass2 (sourceLine,inst,addr,map)
      | AppleCore inst => AppleCore.pass2 (sourceLine,inst,addr,map)

end

  
