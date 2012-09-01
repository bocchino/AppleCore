structure Error : ERROR =
struct

val progName = "sc-avm-asm"

datatype t =
	 BadAddress
       | BadLabel
       | FileNotFound of string
       | InvalidMnemonic of string
       | NoGlobalLabel
       | NoLabel
       | RangeError
       | RedefinedLabel of {file:string,lineNum:int}
       | UndefinedLabel
       | UnsupportedDirective of string

exception AssemblyError of t

fun report err =
    print (progName ^ ": " ^ err ^ "\n")
    
fun show {line,name,lineNum,exn as AssemblyError err} = 
    report("at line " ^ (Int.toString lineNum) ^ " of " ^ 
	   name ^ ":\n" ^
	   (case err of 
		BadAddress => "bad address"
	      | BadLabel => "bad label"
	      | FileNotFound file => "file " ^ file ^ " not found"
	      | InvalidMnemonic mem => "invalid mnemonic " ^ mem
	      | NoGlobalLabel => "local label with no preceding global label"
	      | NoLabel => "no label"
	      | RangeError => "range error"
	      | RedefinedLabel {file,lineNum} => 
		"redefined label; original definition at line " ^
		(Int.toString lineNum) ^ " of " ^ file
	      | UndefinedLabel => "undefined label"
	      | UnsupportedDirective dir => "unsupported directive " ^ dir))
  | show _ = report "exception occurred"

end

