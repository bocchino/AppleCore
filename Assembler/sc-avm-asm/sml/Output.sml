structure Output : OUTPUT =
struct

type data = int * Word8Vector.vector list

structure Map = SplayMapFn(struct
			   type ord_key = string
			   val compare = String.compare
			   end)

type dataMap = data Map.map

type t = {dir:string,file:string,origin:int,dataMap:dataMap}

(* Convert file names to uppercase *)
fun toUpper str =
    String.implode (List.map Char.toUpper (String.explode str))

fun new {dir,file,origin} =
    {dir=dir,
     file=toUpper file,
     origin=origin,
     dataMap=Map.empty}

fun TF ({dir,file,origin,dataMap},newFile,newOrigin) =
    {dir=dir,
     file=toUpper newFile,
     origin=newOrigin,
     dataMap=dataMap}

fun addBytes ({dir,file,origin,dataMap},bytes) =
    let
	val bytes = Word8Vector.fromList (List.map Word8.fromInt bytes)
    in
	{dir=dir,
	 file=file,
	 origin=origin,
	 dataMap=case Map.find (dataMap,file) of
		     SOME (_,oldBytes) => 
		     Map.insert (dataMap,file,(origin,bytes :: oldBytes))
		   | NONE => Map.insert (dataMap,file,(origin,[bytes]))}
    end

val mappingHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" ^
		    "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \
		    \\"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" ^
		    "<plist version=\"1.0\">\n" ^
		    "<array>\n"

val mappingFooter = "</array>\n" ^
		    "</plist>\n"

fun write ({dir,dataMap,...}:t) =
    let
	val mappingStream = TextIO.openOut (dir ^ "/_AppleDOSMappings.plist")
	fun writeHeader () = TextIO.output (mappingStream,mappingHeader)
	fun writeDict (name,addr) =
	    let
		fun printLine 0 line = TextIO.output (mappingStream,line ^ "\n")
		  | printLine n line = (TextIO.output (mappingStream,"\t"); 
					printLine (n-1) line)
	    in
		(printLine 1 "<dict>";
		 printLine 2 "<key>DOSFileType</key>";
		 printLine 2 "<integer>66</integer>";
		 printLine 2 "<key>Filename</key>";
		 printLine 2 ("<string>" ^ name ^ "</string>");
		 printLine 2 "<key>MacFormat</key>";
		 printLine 2 "<integer>2</integer>";
		 printLine 2 "<key>StartingAddress</key>";
		 printLine 2 ("<integer>" ^ (Int.toString addr) ^ "</integer>");
		 printLine 2 "<key>Valid</key>";
		 printLine 2 "<true/>";
		 printLine 2 "<key>Verified</key>";
		 printLine 2 "<true/>";
		 printLine 1 "</dict>")
	    end
	fun writeFooter () = TextIO.output (mappingStream,mappingFooter)
	fun writeFile (file:string,(origin,bytes):data) =
	    if (List.length bytes > 0) then
		let 
		    val path = dir ^ "/" ^ file
		    val stream = BinIO.openOut path
		    fun writeVector vec = BinIO.output (stream,vec) 
		in
		    (List.app writeVector (List.rev bytes);
		     BinIO.closeOut stream;
		     writeDict (file,origin))
		end
	    else ()
    in
	(writeHeader();
	 Map.appi writeFile dataMap;
	 writeFooter();
	 TextIO.closeOut mappingStream)
    end
end

