structure Output : OUTPUT =
struct

type data = Word8Vector.vector list

(* Convert file names to all uppercase for comparison *)
fun normalize str =
    String.implode (List.map Char.toUpper (String.explode str))

structure Map = SplayMapFn(struct
			   type ord_key = string
			   val compare = fn (s1,s2) => 
					    String.compare (normalize s1, normalize s2)
			   end)

type dataMap = data Map.map

type t = {dir:string,file:string,dataMap:dataMap}

fun new {dir,file} =
    {dir=dir,
     file=file,
     dataMap=Map.empty}

fun tf ({dir,file,dataMap},newFile) =
    {dir=dir,
     file=newFile,
     dataMap=dataMap}

fun addBytes ({dir,file,dataMap},data) =
    let
	val data = Word8Vector.fromList (List.map Word8.fromInt data)
    in
	{dir=dir,
	 file=file,
	 dataMap=case Map.find (dataMap,file) of
		     SOME oldData => Map.insert (dataMap,file,data :: oldData)
		   | NONE => Map.insert (dataMap,file,[data])}
    end

fun write {dir,file,dataMap} = 
    let
	fun writeFile (file:string,data:data) =
	    if (List.length data > 0) then
		let 
		    val path = dir ^ "/" ^ file
		    val stream = BinIO.openOut path
		    fun writeVector vec = BinIO.output (stream,vec) 
		in
		    (List.app writeVector (List.rev data);
		     BinIO.closeOut stream)
		end
	    else ()
    in
	Map.appi writeFile dataMap
    end
end

