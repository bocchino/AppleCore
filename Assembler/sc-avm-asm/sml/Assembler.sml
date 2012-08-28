structure Assembler : ASSEMBLER = 
struct

exception BadArgument

datatype optResult = IncludeOpt of string
		   | OutputOpt of string

val options = [{short="i",
		long=[],
		desc=GetOpt.ReqArg (IncludeOpt,"p1:...:pn"),
		help="search paths p1,...,pn for included files"},
	       {short="o",
		long=[],
		desc=GetOpt.ReqArg (OutputOpt,"outfile"),
		help="write output to outfile"}]

fun processOpts opts  =
    let
	fun isColon c = (c = #":")
	fun processOpts' {outFile:string option,
			  paths:string list} 
			 opts =
	    case opts of
		[] => {outFile=outFile,paths=paths}
	      | (IncludeOpt newPaths)::opts' =>
		processOpts' {outFile=outFile, paths=(String.tokens isColon newPaths) @ paths} opts'
	      | (OutputOpt newOutFile)::opts' =>
		(case outFile of
		     NONE => processOpts' {outFile=SOME newOutFile,paths=paths} opts'
		   | _ => raise BadArgument)
    in
	processOpts' {outFile=NONE,paths=["."]} opts
    end

fun main(name,args) =
    ((case GetOpt.getOpt {argOrder=GetOpt.Permute,
			  options=options,
			  errFn= fn s => print s} 
			 args of
	  (opts,[inFile]) => 
	  let
	      val {outFile,paths} = processOpts opts
	  in 
	      Parser.parseFile paths inFile
	  end
	| _ => raise BadArgument)
	 handle BadArgument => 
		print (GetOpt.usageInfo
			   {header = "usage: sc-avm-asm infile [opts]",
			    options = options});
     OS.Process.success)

end
