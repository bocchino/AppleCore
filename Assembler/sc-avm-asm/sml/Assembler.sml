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
	val defaultOpts = {outFile=NONE,paths=["."],list=false}
	fun isColon c = (c = #":")
	fun processOpts {outFile,paths,list} opts =
	    case opts of
		ListOpt :: opts => 
		processOpts {outFile=outFile,
			     paths=paths,
			     list=true} opts
	      |	(IncludeOpt newPaths) :: opts => 
		processOpts 
		    {outFile=outFile,
		     paths=(String.tokens isColon newPaths) @ paths,
		     list=list} opts
	      | (DirOpt newOutFile) :: opts =>
		(case outFile of
		     NONE => processOpts {outFile=SOME newOutFile,
					  paths=paths,
					  list=list} opts
		   | _ => raise BadArgument)
	      | _ => {outFile=outFile,
		      paths=paths,
		      list=list}

    in
	processOpts defaultOpts opts
    end

fun assemble ({outFile,paths,list},inFile) =
    let
	fun pass1 (file,addr,map,instList) =
	    case File.nextLine file of
		NONE => (List.rev instList,map)
	      |	SOME (line,file) => parse (file,line,addr,map,instList)
	and parse (file,line,addr,map,instList) =
	    case Line.parse (file,line) of
		(line,file) => pass1' (file,line,addr,map,instList)
	and pass1' (file,line,addr,map,instList) =
	    case Line.pass1 (line,addr,map) of
		(line,addr',map) => pass1 (file,addr',map,(line,addr) :: instList)
	fun pass2 ([],map) = ()
	  | pass2 ((line,addr)::rest,map) = (Line.pass2(line,addr,map,list);
					     pass2 (rest,map))
	val file = File.openIn paths inFile
	    handle e as AssemblyError (FileNotFound file) => 
		   (Error.report ("file " ^ file ^ " not found");
		    raise e)
    in
	pass2 (pass1 (file,0x800,Label.fresh,[]))
    end

fun main(name,args) =
    ((case GetOpt.getOpt {argOrder=GetOpt.Permute,
			  options=options,
			  errFn= fn s => print s} 
			 args of
	  (opts,[inFile]) => assemble (processOpts opts,inFile)
       | _ => raise BadArgument);
     OS.Process.success)
    handle BadArgument => (print (GetOpt.usageInfo
				      {header = "usage: sc-avm-asm infile [opts]",
				       options = options}); OS.Process.failure)
end
