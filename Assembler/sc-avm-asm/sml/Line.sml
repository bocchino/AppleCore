structure Line : LINE =
struct

open Error
open TextIO

type t = (Label.t option) * (Instruction.t option)

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
	val line' = parseLine (File.data line)
    in 
	case line' of
	    (_,SOME inst) => (line', Instruction.includeIn inst file)
	  | _ => (line',file)
    end
    handle e => (Error.show {line=(File.data line),name=(File.fileName line),
			     lineNum=(File.lineNumber line),exn=e}; (emptyLine,file)) 

fun pass1 (sourceLine,line,addr,map) = 
    let 
	val source = {sourceLine=sourceLine,
		      address=addr}
	fun add (map,label,source) =
	    case label of
		SOME label => Label.add (map,label,source)
	      | NONE => map
    in
	case line of
	    (SOME label,NONE) => (addr,Label.add(map,label,source), NONE)
	  | (label,SOME inst) => 
	    let
		val map = add (map,label,source)
		val (inst,addr,map) = Instruction.pass1 (label,inst) (source,map)
	    in
		(addr,map,SOME inst)
	    end
	  | _ => (addr,map,NONE)
    end
    
end

  
