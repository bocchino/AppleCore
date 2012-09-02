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

fun formatBytes bytes =
    let
	fun formatBytes n bytes =
	    if n = 0 then "" else case bytes of
				      byte::rest => (formatHexByte byte) ^ " " ^ (formatBytes (n-1) rest)
				    | [] => "   " ^ (formatBytes (n-1) [])
    in
	formatBytes 3 bytes
    end

fun formatLine (addr,bytes,line) =
    (case addr of 
	 SOME addr => formatAddress addr
       | NONE => formatBlankAddress()) ^
    formatBytes bytes ^ "\t" ^
    line

end
