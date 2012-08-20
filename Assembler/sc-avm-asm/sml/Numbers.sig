signature NUMBERS =
sig

    exception RangeError

    val parseDigits : Substring.substring -> 
		      StringCvt.radix -> 
		      (int * Substring.substring) option
    val parseNumber : Substring.substring -> (int * Substring.substring) option
    val normalize : int -> int -> int					     

end
