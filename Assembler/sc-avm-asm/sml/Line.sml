structure Line : LINE =
struct

open Error
open TextIO

type t = File.line * (Label.t option) * (Instruction.t option)

val emptyLine = (NONE,NONE)

fun parseNoLabel substr =
    case Instruction.parse substr of
	SOME i => (NONE,SOME i)
      | _      => emptyLine

fun parseLabel substr =
    case Label.parse substr of
	NONE => raise AssemblyError BadLabel
      | SOME (l,rest) => (SOME l,Instruction.parse rest)

fun parseLine line =
    let
	val substr = Substring.dropr Char.isSpace (Substring.full line)
    in
	case Substring.getc substr of
	    NONE => emptyLine
	  | SOME (c,rest) =>
	    if Char.isSpace c 
	    then parseNoLabel rest
	    else 
		if c = #"*" orelse c = #":" then emptyLine
		else parseLabel substr
    end

fun parse (file,line) =
    let
	val (label,inst) = parseLine (File.data line)
	val file = case inst of
		       SOME inst' => Instruction.includeIn inst' file
		     | _ => file
    in 
	((line,label,inst),file)
    end
    handle e => (Error.show {line=(File.data line),name=(File.fileName line),
			     lineNum=(File.lineNumber line),exn=e}; raise e)

fun pass1 (line as (sourceLine,label,inst),addr,map) = 
    let 
	val source = {sourceLine=sourceLine,
		      address=addr}
	fun add () =
	    case label of
		SOME label => Label.add (map,label,source)
	      | NONE => map
    in
	case (label,inst) of
	    (SOME label,NONE) => (line,addr,Label.add(map,label,source))
	  | (label,SOME inst) => 
	    let
		val map = add ()
		val (inst,addr,map) = Instruction.pass1 (label,inst) (source,map)
	    in
		((sourceLine,label,SOME inst),addr,map)
	    end
	  | _ => (line,addr,map)
    end
    handle e => (Error.show {line=(File.data sourceLine),name=(File.fileName sourceLine),
			     lineNum=(File.lineNumber sourceLine),exn=e}; raise e)

fun pass2 (line as (sourceLine,label,inst),addr,map,listFn) = 
    (case (label,inst) of
	(_,SOME inst) => Instruction.pass2 (sourceLine,inst,addr,map,listFn)
      | (SOME label,NONE) => listFn (Printing.formatLine (SOME addr,[],File.data sourceLine))
      | _ => listFn (Printing.formatLine (NONE,[],File.data sourceLine)))
    handle e => (Error.show {line=(File.data sourceLine),name=(File.fileName sourceLine),
			     lineNum=(File.lineNumber sourceLine),exn=e}; raise e)

end

  
