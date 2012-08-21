signature NUMBERS =
sig

    exception RangeError

    val MAX_INT : IntInf.int

    val parseDigits : Substring.substring -> 
		      StringCvt.radix -> 
		      (IntInf.int * Substring.substring) option
    val parse : Substring.substring -> (IntInf.int * Substring.substring) option
    val normalize : int -> IntInf.int -> int				     

end
