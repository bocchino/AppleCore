structure Printing : PRINTING =
struct

fun formatHexByte byte =
    (if (byte < 16) then "0" else "") ^
    (Int.fmt StringCvt.HEX byte)

fun formatHexWord word =
    (formatHexByte (word div 256)) ^
    (formatHexByte (word mod 256))

fun formatAddress address =
    (formatHexWord address) ^ "- "

fun formatBlankAddress () =
    "     "

val LINE_LEN = 3

fun splitLine bytes =
    if (List.length bytes <= LINE_LEN) then
	(bytes,[])
    else
	(List.take (bytes,LINE_LEN),List.drop (bytes,LINE_LEN))

fun formatBytes bytes =
    let fun formatBytes n bytes = 
	    if n = 0 
	    then 
		""
	    else
		case bytes of
		    [] => "   "
		  | (byte :: rest) => 
		    (formatHexByte byte) ^ " " ^ (formatBytes (n-1) rest)
    in
	formatBytes LINE_LEN bytes
    end

fun formatLine (addr,bytes,line) =
    let
	val addrString = case addr of 
			     SOME addr => formatAddress addr
			   | NONE => formatBlankAddress()
	val (first,rest) = splitLine bytes
	val formatRest = case (addr,rest) of
			     (_,[]) => ""
			   | (SOME addr,_) => 
			     formatLine (SOME (addr+3),rest,"\n")
			   | (NONE,_) => formatLine(NONE,rest,"\n")
    in
	addrString ^ formatBytes first ^ "\t" ^ line ^ 
	formatRest
    end

end
