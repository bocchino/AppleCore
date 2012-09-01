signature NUMBERS =
sig

    val MAX_INT : IntInf.int

    val parseDigits : Substring.substring -> 
		      StringCvt.radix -> 
		      (IntInf.int * Substring.substring) option
    val parse : Substring.substring -> (IntInf.int * Substring.substring) option
    val normalize : int -> IntInf.int -> int				     
    val parseArg : Substring.substring -> IntInf.int
    val isZeroPage : int -> bool
    val sizeOf : IntInf.int -> int
    val lowByte : int -> int
    val highByte : int -> int
    val bytes : int -> int list

end
