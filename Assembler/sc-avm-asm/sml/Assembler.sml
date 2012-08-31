structure Assembler : ASSEMBLER = 
struct

exception BadArgument

open Error

datatype optResult = IncludeOpt of string
		   | ListOpt
		   | DirOpt of string

val options = [{short="i",
		long=[],
		desc=GetOpt.ReqArg (IncludeOpt,"p1:...:pn"),
		help="search paths p1,...,pn for included files"},
	       {short="d",
		long=[],
		desc=GetOpt.ReqArg (DirOpt,"outdir"),
		help="write output to outdir"},
	       {short="l",
		long=[],
		desc=GetOpt.NoArg (fn () => ListOpt),
		help="list assembled program to stdout"}]

fun processOpts opts  =
    let
	fun isColon c = (c = #":")
	fun processOpts (outFile,paths,opts) =
	    case opts of
		(IncludeOpt newPaths)::opts =>
		processOpts (outFile,(String.tokens isColon newPaths) @ paths,opts)
	      | (DirOpt newOutFile)::opts =>
		(case outFile of
		     NONE => processOpts (SOME newOutFile,paths,opts)
		   | _ => raise BadArgument)
	      | _ => (outFile,paths)

    in
	processOpts (NONE,["."],opts)
    end

fun assemble (paths,fileName) =
    let
	fun assemble (file,addr,map) =
	    case File.nextLine file of
		NONE => ()
	      |	SOME (sourceLine,file) => parse (file,sourceLine,addr,map)
	and parse (file,sourceLine,addr,map) =
	    case Line.parse (file,sourceLine) of
		(line,file) => pass1 (file,sourceLine,line,addr,map)
	and pass1 (file,sourceLine,line,addr,map) =
	    case Line.pass1 (sourceLine,line,addr,map) of
		(addr',map,inst) => (Line.list (sourceLine,line,addr);
				     assemble (file,addr',map))
    in
	(assemble ((File.openIn paths fileName),0x800,Label.fresh);
	 OS.Process.success)
	handle AssemblyError (FileNotFound file) => 
	       (Error.report ("file " ^ file ^ " not found");
		OS.Process.failure)
    end

fun main(name,args) =
    (case GetOpt.getOpt {argOrder=GetOpt.Permute,
			 options=options,
			 errFn= fn s => print s} 
			args of
	 (opts,[inFile]) => 
	 let
	     val (outFile,paths) = processOpts opts
	 in 
	     assemble (paths,inFile)
	 end
       | _ => raise BadArgument)
    handle BadArgument => 
	   (print (GetOpt.usageInfo
		      {header = "usage: sc-avm-asm infile [opts]",
		       options = options}); OS.Process.failure)

end
