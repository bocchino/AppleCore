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

(* Make a Word8 vector from a list of bytes *)
fun makeVector bytes = Word8Vector.fromList (List.map Word8.fromInt bytes)

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
	val bytes = makeVector bytes
    in
	{dir=dir,
	 file=file,
	 origin=origin,
	 dataMap=case Map.find (dataMap,file) of
		     SOME (_,oldBytes) => 
		     Map.insert (dataMap,file,(origin,bytes :: oldBytes))
		   | NONE => Map.insert (dataMap,file,(origin,[bytes]))}
    end

fun write ({dir,dataMap,...}:t) =
    let
	fun writeFile (file:string,(origin,bytes):data) =
	    let
		val length = List.foldl op+ 0 (List.map Word8Vector.length bytes)
	    in
		if length > 0 then
		    let 
			val path = dir ^ "/" ^ file
			val stream = BinIO.openOut path
			fun writeVector vec = BinIO.output (stream,vec) 
		    in
			(writeVector (makeVector (Numbers.bytes origin));
			 writeVector (makeVector (Numbers.bytes length));
			 List.app writeVector (List.rev bytes);
			 BinIO.closeOut stream)
		    end
		else ()
	    end
    in
	Map.appi writeFile dataMap
    end
end

