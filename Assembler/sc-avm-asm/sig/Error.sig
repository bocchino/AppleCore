signature ERROR =
sig

    exception BadAddress
    exception BadLabel
    exception FileNotFound of string
    exception InvalidMnemonic of string
    exception NoLabel
    exception RangeError
    exception UndefinedLabel
    exception UnsupportedDirective of string

    val show : {line:string,
		name:string,
		number:int,
		exn:exn} -> unit

end
