structure Error : ERROR =
struct

val progName = "sc-avm-asm"

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

fun assemblyErrString err = case err of 
			BadAddress => "bad address"
		      | BadLabel => "bad label"
		      | FileNotFound file => "file " ^ file ^ " not found"
		      | InvalidMnemonic => "invalid mnemonic "
		      | NoGlobalLabel => "local label with no preceding global label"
		      | NoLabel => "no label"
		      | RangeError => "range error"
		      | RedefinedLabel {file,lineNum} => 
			"redefined label; original definition at line " ^
			(Int.toString lineNum) ^ " of " ^ file
		      | UndefinedLabel => "undefined label"
		      | UnsupportedDirective => "unsupported directive "


fun errString (AssemblyError err) = assemblyErrString err
  | errString (LineError {fileName,lineNum,lineData,err}) =
    "at line " ^ (Int.toString lineNum) ^ " of " ^ fileName ^ ":\n" ^ 
    lineData ^ (errString err)
  | errString _ = "unknown error occurred"

fun show err =
    print (progName ^ ": " ^ (errString err) ^ "\n")

end

