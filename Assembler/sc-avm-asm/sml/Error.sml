structure Error : ERROR =
struct

    exception BadAddress
    exception BadLabel
    exception InvalidMnemonic of string
    exception RangeError
    exception UnsupportedDirective

end

