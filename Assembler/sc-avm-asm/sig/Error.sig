
(* Module representing assembler errors *)

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

    (* An error that occurred during assembly *)
    exception AssemblyError of t

    (* An error that occurred during the processing
       of a line *)
    exception LineError of {fileName:string,
			    lineNum:int,
			    lineData:string,
			    err:exn}

    (* Print an error message *)
    val show : exn -> unit

end
