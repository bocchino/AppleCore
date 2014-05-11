
(* Module for processing numbers *)

signature NUMBERS =
sig

    (* Maximum integer value allowed in AppleCore = 2^(255*8) *)
    val MAX_INT : IntInf.int

    (* Parse a number, including negative and hex numbers *)
    val parse : Substring.substring -> 
                (IntInf.int * Substring.substring) option

    (* Parse numeric digits, given a radix *)
    val parseDigits : Substring.substring -> 
                      StringCvt.radix -> 
                      (IntInf.int * Substring.substring) option

    (* Normalize a number to the given range.
       Throw 'range error' if number is out of range. *)
    val normalize : int -> IntInf.int -> int

    (* Parse a numeric argument of an expression *)
    val parseArg : Substring.substring -> IntInf.int

    (* Report whether a number represents a zero-page address *)
    val isZeroPage : int -> bool

    (* Compute the size of a number in bytes *)
    val sizeOf : IntInf.int -> int

    (* Return the low byte of a 16-bit number *)
    val lowByte : int -> int

    (* Return the high byte of a 16-bit number *)
    val highByte : int -> int

    (* Turn a number into the corresponding list of
       bytes, in little endian order. *)
    val bytes : int -> int list
    val constBytes : IntInf.int -> int list

end
