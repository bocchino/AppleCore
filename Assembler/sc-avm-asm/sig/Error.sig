signature ERROR =
sig

    exception BadAddress
    exception BadLabel
    exception FileNotFound of string
    exception InvalidMnemonic of string
    exception RangeError
    exception UndefinedLabel
    exception UnsupportedDirective of string

    val show : string -> int -> exn -> unit

end
