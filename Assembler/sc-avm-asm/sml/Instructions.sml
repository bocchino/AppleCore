structure Instructions : INSTRUCTIONS =
  struct

  datatype instruction =
	   Native of Native.instruction
	 | Directive of Directives.directive
	 | AppleCore of AppleCore.instruction

  fun apply parser substr =
      parser (Substring.dropl Char.isSpace substr)
			
  fun parse substr = 
      case apply Native.parse substr of
	  SOME inst => SOME (Native inst)
        | _  => (
	  case apply AppleCore.parse substr of
	      SOME inst => SOME (AppleCore inst)
	    | _ => (
	      case apply Directives.parse substr of
		  SOME inst => SOME (Directive inst)
		| _ => NONE
	      )
	  )

  end

  
