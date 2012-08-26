signature ERROR =
sig

    exception BadAddress
    exception BadLabel
    exception InvalidMnemonic of string
    exception RangeError
    exception UnsupportedDirective of string

    val show : int -> exn -> unit

end
