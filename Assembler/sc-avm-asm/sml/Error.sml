structure Error : ERROR =
struct

datatype t =
	 BadAddress
       | BadLabel
       | FileNotFound of string
       | InvalidMnemonic of string
       | NoLabel
       | RangeError
       | RedefinedLabel of {file:string,line:int}
       | UndefinedLabel
       | UnsupportedDirective of string

exception AssemblyError of t

fun show {line,name,number,exn as AssemblyError err} = 
    (print ("at line " ^ (Int.toString number) ^ " of " ^ name ^ ":\n");
     print line;
     print (case err of 
		BadAddress => "bad address"
	      | BadLabel => "bad label"
	      | FileNotFound file => "file " ^ file ^ " not found"
	      | InvalidMnemonic mem => "invalid mnemonic " ^ mem
	      | NoLabel => "no label"
	      | RangeError => "range error"
	      | RedefinedLabel {file,line} => 
		"redefined label; original definition at line " ^
		(Int.toString number) ^ " of " ^ file
	      | UndefinedLabel => "undefined label"
	      | UnsupportedDirective dir => "unsupported directive " ^ dir);
     print "\n")
  | show {line,name,number,exn} = raise exn
    
end

