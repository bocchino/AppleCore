signature NUMBERS =
sig

    val MAX_INT : IntInf.int

    val formatHexByte : int -> string
    val formatHexWord : int -> string
    val formatAddress : int -> string
    val formatBlankAddress : unit -> string

    val parseDigits : Substring.substring -> 
		      StringCvt.radix -> 
		      (IntInf.int * Substring.substring) option
    val parse : Substring.substring -> (IntInf.int * Substring.substring) option
    val normalize : int -> IntInf.int -> int				     
    val parseArg : Substring.substring -> IntInf.int
    val isZeroPage : int -> bool
    val sizeOf : IntInf.int -> int

end
