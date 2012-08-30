signature ERROR =
sig

    datatype t =
	     BadAddress
	   | BadLabel
	   | FileNotFound of string
	   | InvalidMnemonic of string
	   | NoLabel
	   | RangeError
	   | RedefinedLabel of {file:string,lineNum:int}
	   | UndefinedLabel
	   | UnsupportedDirective of string

    exception AssemblyError of t

    val show : {line:string,
		name:string,
		lineNum:int,
		exn:exn} -> unit

end
