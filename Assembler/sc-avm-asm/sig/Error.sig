signature ERROR =
sig

    datatype t =
	     BadAddress
	   | BadLabel
	   | FileNotFound of string
	   | InvalidMnemonic
	   | NoGlobalLabel
	   | NoLabel
	   | RangeError
	   | RedefinedLabel of {file:string,lineNum:int}
	   | UndefinedLabel
	   | UnsupportedDirective

    exception AssemblyError of t
    exception LineError of {fileName:string,
			    lineNum:int,
			    lineData:string,
			    err:exn}

    val show : exn -> unit

end
