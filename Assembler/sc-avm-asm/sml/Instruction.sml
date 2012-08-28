structure Instruction : INSTRUCTION =
  struct

  open Error

  datatype t =
	   Native of Native.instruction
	 | Directive of Directives.directive
	 | AppleCore of AppleCore.instruction

  fun apply parser substr =
      parser (Substring.dropl Char.isSpace substr)
			
  fun parse' substr = 
      case apply Native.parse substr of
	  SOME inst => SOME (Native inst)
        | _  => (case apply AppleCore.parse substr of
		     SOME inst => SOME (AppleCore inst)
		   | _ => (case apply Directives.parse substr of
			       SOME inst => SOME (Directive inst)
			     | _ => NONE))

  fun parse substr =
      case parse' substr of
          SOME (Directive Directives.Ignored) => NONE
	| SOME i => SOME i
	| _  =>
	  let
	      val rest = Substring.dropl Char.isSpace substr
	  in
	      if Substring.isEmpty rest
	      then NONE
	      else raise InvalidMnemonic 
			     (Substring.string (Substring.takel (not o Char.isSpace) rest))
	  end

  fun includeIn paths file inst =
      case inst of
	  Directive (Directives.IN name) =>
	  File.includeIn paths file name
	| _ => file
		       
  end

  
