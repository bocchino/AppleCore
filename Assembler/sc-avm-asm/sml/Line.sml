structure Line : LINE =
struct

open Error
open TextIO

type t = (Label.t option) * (Instruction.t option)

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

fun parse (paths,file,line) =
    let
	val line = parseLine line
    in 
	case line of
	    SOME (line as (_,SOME inst)) => SOME (line, Instruction.includeIn inst (paths,file))
	  | SOME line => SOME (line,file)
	  | _ => NONE
    end
    handle e => (Error.show {line=line,name=(File.name file),
			     number=(File.line file),exn=e}; NONE)

fun pass1 (file,line,addr,map) = 
    let 
	val source = {file=File.name file,
		      line=File.line file,
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

  
