structure Assembler : ASSEMBLER = 
struct

exception BadArgument

open Error

datatype optResult = IncludeOpt of string
                   | ListOpt
                   | DirOpt of string
                   | OutFileOpt of string

val options = [{short="d",
                long=[],
                desc=GetOpt.ReqArg (DirOpt,"outdir"),
                help="set output directory"},
               {short="i",
                long=[],
                desc=GetOpt.ReqArg (IncludeOpt,"p1:...:pn"),
                help="search paths p1,...,pn for included files"},               
               {short="l",
                long=[],
                desc=GetOpt.NoArg (fn () => ListOpt),
                help="list assembly to stdout"},
               {short="o",
                long=[],
                desc=GetOpt.ReqArg (OutFileOpt,"outfile"),
                help="set output file"}]

fun processOpts (opts,inFile)  =
    let
        val defaultOpts = {outDir=NONE,
                           outFile=NONE,
                           paths=["."],
                           list=false}
        fun isColon c = (c = #":")
        fun processOpts {outDir,outFile,paths,list} opts =
            case opts of
                ListOpt :: opts => 
                processOpts {outDir=outDir,
                             outFile=outFile,
                             paths=paths,
                             list=true} opts
              |        (IncludeOpt newPaths) :: opts => 
                processOpts 
                    {outDir=outDir,
                     outFile=outFile,
                     paths=(String.tokens isColon newPaths) @ paths,
                     list=list} opts
              | (DirOpt newOutDir) :: opts =>
                (case outDir of
                     NONE => processOpts {outDir=SOME newOutDir,
                                          outFile=outFile,
                                          paths=paths,
                                          list=list} opts
                   | _ => raise BadArgument)
              | (OutFileOpt newOutFile) :: opts =>
                (case outFile of
                     NONE => processOpts {outDir=outDir,
                                          outFile=SOME newOutFile,
                                          paths=paths,
                                          list=list} opts
                   | _ => raise BadArgument)
              | _ => {inFile=inFile,
                      outDir=outDir,
                      outFile=outFile,
                      paths=paths,
                      list=list}

    in
        processOpts defaultOpts opts
    end

fun assemble {inFile,outDir,outFile,paths,list} =
    let
        val inFileRoot = let
            val substr = Substring.full inFile
            val substr = Substring.taker (fn c => not (c = #"/")) substr
            val substr = if (Substring.isSuffix ".avm" substr) then
                             Substring.trimr 4 substr 
                         else
                             substr
        in
            Substring.string substr
        end
        val outDir = case outDir of
                         SOME outDir => outDir
                       | NONE => "."
        val outFile = case outFile of
                          SOME outFile => outFile
                        | NONE => inFileRoot ^ ".obj"
        val listFn =
            if list then fn s => print s else fn s => ()
        fun pass1 (file,addr,map,instList) =
            case File.nextLine file of
                NONE => (List.rev instList,map)
              |        SOME (line,file) => parse (file,line,addr,map,instList)
        and parse (file,line,addr,map,instList) =
            case Line.parse (file,line) of
                (line,file) => pass1' (file,line,addr,map,instList)
        and pass1' (file,line,addr,map,instList) =
            case Line.pass1 (line,addr,map) of
                (line,addr',map) => pass1 (file,addr',map,(line,addr) :: instList)
        fun pass2 output ([],map) = output
          | pass2 output ((line,addr)::rest,map) = 
            let
                val (output,listing) = Line.pass2(output,line,addr,map)
            in
                (listFn listing; pass2 output (rest,map))
            end
        val file = File.openIn paths inFile
        val defaultOrigin = 0x800
        val pass1Result = pass1 (file,defaultOrigin,Label.fresh,[]) 
        val output = pass2 (Output.new {dir=outDir,
                                        file=outFile,
                                        origin=defaultOrigin}) pass1Result
    in
        Output.write output
    end

fun main(name,args) =
    ((case GetOpt.getOpt {argOrder=GetOpt.Permute,
                          options=options,
                          errFn= fn s => print s} 
                         args of
          (opts,[inFile]) => assemble (processOpts (opts,inFile))
       | _ => raise BadArgument);
     OS.Process.success)
    handle BadArgument => (print (GetOpt.usageInfo
                                      {header = "usage: sc-avm-asm infile [opts]",
                                       options = options}); OS.Process.failure)
         | e => (Error.show e; OS.Process.failure)

end
