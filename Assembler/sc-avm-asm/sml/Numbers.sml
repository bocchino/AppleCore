structure Numbers : NUMBERS =
struct

exception RangeError

fun parseDigits substr radix =
    case radix of 
	StringCvt.HEX =>
	let 
            val (num,substr') = Substring.splitl Char.isHexDigit substr
        in
	    case StringCvt.scanString(Int.scan StringCvt.HEX) 
				     (Substring.string num) of
		SOME n => SOME (n,substr')
	      | NONE   => NONE
	end
      | StringCvt.DEC =>
	let
	    val (num,substr') = Substring.splitl Char.isDigit substr
        in
	    case Int.fromString (Substring.string num) of
		SOME n => SOME (n,substr')
              | NONE   => NONE
        end
      | _ => NONE
	     
fun parseNumber substr = 
    (case Substring.getc substr of
	 SOME (#"-",substr') =>
         let 
	     val n = parseNumber substr'
         in 
	     case n of
		 NONE              => NONE
	       | SOME (n',substr'') => SOME (~n',substr'')
	 end
       | SOME(#"$",substr') => 
	 parseDigits substr' StringCvt.HEX 
       | _ =>
	 parseDigits substr StringCvt.DEC)
    handle Overflow => raise RangeError

fun normalize bound num =
    if num <= ~bound orelse num >= bound then
	raise RangeError
    else if num < 0 then
	bound + num
    else num

end
