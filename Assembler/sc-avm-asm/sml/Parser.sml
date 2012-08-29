structure Parser : PARSER =
struct

open Error
open TextIO

type line = (Label.t option) * (Instruction.t option)

fun parseNoLabel substr =
    case Instruction.parse substr of
	SOME i => SOME (NONE, SOME i)
      | _      => NONE

fun parseLabel substr =
    case Label.parse substr of
	NONE => raise AssemblyError BadLabel
      | SOME (l,rest) => SOME (SOME l,Instruction.parse rest)

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
		 SOME (_,SOME inst) =>
		 SOME (line, Instruction.includeIn inst (paths,file))
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

  
