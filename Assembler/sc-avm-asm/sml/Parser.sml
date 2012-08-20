structure Parser : PARSER =
struct

type line = (Labels.label option) * (Instructions.instruction option)

fun parseInstruction substr =
    case Instructions.parse substr of
        SOME (Instructions.Directive Directives.Ignored) => NONE
      |	SOME i => SOME i
      | _      =>
	if Substring.isEmpty (Substring.dropl Char.isSpace substr)
	then NONE
	else raise Instructions.BadOpcodeError

fun parseNoLabel substr =
    case parseInstruction substr of
	SOME i => SOME (NONE, SOME i)
      | _      => NONE

fun parseLabel substr =
    case Labels.parse substr of
	NONE => raise Labels.BadLabelError
      | SOME (l,rest) => SOME (SOME l,parseInstruction rest)

fun parseLine str =
    let
	val substr = Substring.full str
    in
	case Substring.getc substr of
	    NONE => NONE
	  | SOME (c,rest) =>
	    if Char.isSpace c 
	    then parseNoLabel rest
	    else 
		if (c = #"*") then NONE
		else parseLabel substr
    end

end

  
