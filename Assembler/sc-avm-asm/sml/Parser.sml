structure Parser : PARSER =
struct

type line = (Labels.label option) * (Instructions.instruction option)

fun parseInstruction substr =
    case Instructions.parse substr of
	SOME i => SOME i
      | _      =>
	if Substring.isEmpty (Substring.dropl Char.isSpace substr)
	then NONE
	else raise Instructions.BadOpcodeError

fun parseLine str =
    let
	val substr = Substring.full str
    in
	case Substring.getc substr of
	    NONE => NONE
	  | SOME (c,rest) => (
	    if Char.isSpace c 
	    then SOME (NONE,parseInstruction rest)
	    else 
		if (c = #"*") then NONE
		else
		    case Labels.parse substr of
			NONE => raise Labels.BadLabelError
		      | SOME (l,rest') => SOME (SOME l,parseInstruction rest')
	    )
    end

end

  
