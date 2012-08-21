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

fun parseLine line =
    let
	val substr = Substring.full line
    in
	case Substring.getc substr of
	    NONE => NONE
	  | SOME (c,rest) =>
	    if Char.isSpace c 
	    then parseNoLabel rest
	    else 
		if c = #"*" orelse c = #":" then NONE
		else parseLabel substr
    end

fun parseAll file =
    let
	fun parseAll' stream =
	    case TextIO.inputLine stream of
		SOME line => ( 
		parseLine line handle e => (print line; raise e); 
		parseAll' stream 
		)
	      | NONE => ()
    in
	parseAll' (TextIO.openIn file)
    end

end

  
