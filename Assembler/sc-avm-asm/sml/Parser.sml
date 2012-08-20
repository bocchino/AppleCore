structure Parser : PARSER =
struct

type line = (Labels.label option) * (Instructions.instruction option)

fun parseLine str =
    let
	val substr = Substring.full str
    in
	case Substring.getc substr of
	    NONE => NONE
	  | SOME (c,rest) =>
	    if Char.isSpace c 
	    then SOME (NONE,Instructions.parse rest)
	    else 
		if (c = #"*") then NONE
		else (
		    case Labels.parse substr of
			NONE => raise Labels.BadLabelError
		      | SOME (l,rest') => SOME (SOME l,Instructions.parse rest')
		)
    end

end

  
