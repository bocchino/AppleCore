structure Numbers : NUMBERS =
struct

open Error

val MAX_INT = IntInf.<<(1,Word.fromInt (256*8)) - (IntInf.fromInt 1)
val ZERO = IntInf.fromInt 0
val BYTE_SIZE = IntInf.fromInt 256

fun checkRange n =
    if (n < ~MAX_INT orelse n > MAX_INT)
    then raise AssemblyError RangeError
    else n

fun parseDigits substr radix =
    case radix of 
	StringCvt.HEX =>
	let 
            val (num,substr') = Substring.splitl Char.isHexDigit substr
        in
	    case StringCvt.scanString(IntInf.scan StringCvt.HEX) 
				     (Substring.string num) of
		SOME n => SOME (checkRange n,substr')
	      | NONE   => NONE
	end
      | StringCvt.DEC =>
	let
	    val (num,substr') = Substring.splitl Char.isDigit substr
        in
	    case IntInf.fromString (Substring.string num) of
		SOME n => SOME (checkRange n,substr')
              | NONE   => NONE
        end
      | _ => NONE
	     
fun parse substr = 
    (case Substring.getc substr of
	 SOME (#"-",substr') =>
         let 
	     val n = parse substr'
         in 
	     case n of
		 NONE              => NONE
	       | SOME (n',substr'') => SOME (~n',substr'')
	 end
       | SOME(#"$",substr') => 
	 parseDigits substr' StringCvt.HEX 
       | _ =>
	 parseDigits substr StringCvt.DEC)

fun parseArg substr =
    case parse (Substring.dropl Char.isSpace substr) of
	SOME (n,_) => n
      | _          => raise AssemblyError BadAddress
			    
fun normalize bound num =
    let
	val bound' = IntInf.fromInt bound
    in
    if num <= ~bound' orelse num >= bound' then
	raise AssemblyError RangeError
    else 
	let
	    val num' = IntInf.toInt num
	in
	    if num' < 0 then
		bound + num'
	    else num'
	end
    end

fun isZeroPage n =
    n >= 0 andalso n <= 255

fun sizeOf 0 = 1
  | sizeOf n = 
    if n < 0 then
	sizeOf ((~n) * 2)
    else 
	1 + ((IntInf.log2 n) div 8)

fun lowByte num = num mod 256
fun highByte num = num div 256
fun bytes num = [lowByte num,highByte num]
fun constBytes const =
    let fun constBytes bytes const =
	    if (const < BYTE_SIZE) then
		List.rev ((IntInf.toInt const) :: bytes)
	    else
		constBytes ((IntInf.toInt (const mod BYTE_SIZE)) :: bytes) (const div BYTE_SIZE)
    in
	if (const < ZERO) then
	    constBytes [] (~ const)
	else 
	    constBytes [] const
    end

end
