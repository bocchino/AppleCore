structure Parser : PARSER =
struct

open Error
open TextIO

type line = (Label.t option) * (Instruction.t option)

fun parseInstruction substr =
    case Instruction.parse substr of
        SOME (Instruction.Directive Directives.Ignored) => NONE
      |	SOME i => SOME i
      | _      =>
	let
	    val rest = Substring.dropl Char.isSpace substr
	in
	    if Substring.isEmpty rest
	    then NONE
	    else raise InvalidMnemonic 
			   (Substring.string (Substring.takel (not o Char.isSpace) rest))
	end
		       
fun parseNoLabel substr =
    case parseInstruction substr of
	SOME i => SOME (NONE, SOME i)
      | _      => NONE

fun parseLabel substr =
    case Label.parse substr of
	NONE => raise Error.BadLabel
      | SOME (l,rest) => SOME (SOME l,parseInstruction rest)

fun parseLine line =
    let
	val substr = Substring.dropr Char.isSpace (Substring.full line)
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

fun nextLine paths file =
    case File.nextLine file of
	NONE => NONE
      |	SOME (line,file) => 
	(print line;
	 let
	     val line = parseLine line
	 in 
	     case line of
		 SOME (_,SOME (Instruction.Directive (Directives.IN name))) =>
		 SOME (line, File.includeIn paths file name)
	       | _ => SOME (line, file)
	 end)
	handle e => (Error.show {line=line,name=(File.name file),
				 number=(File.line file),exn=e}; NONE)
		    
fun parseFile paths fileName =
    let
	fun parseFile' file =
	    case nextLine paths file of
		SOME (_,file) => parseFile' file
	      | NONE => ()
    in
	parseFile' (File.openIn paths fileName)
    end

end

  
