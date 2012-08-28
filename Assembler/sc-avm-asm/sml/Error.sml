structure Error : ERROR =
struct

exception BadAddress
exception BadLabel
exception FileNotFound of string
exception InvalidMnemonic of string
exception RangeError
exception UndefinedLabel
exception UnsupportedDirective of string
	  
fun show {line,name,number,exn} = 
    (print ("at line " ^ (Int.toString number) ^ " of " ^ name ^ ":\n");
     print line;
     print (case exn of 
		BadAddress => "bad address"
	      | BadLabel => "bad label"
	      | FileNotFound file => "file " ^ file ^ " not found"
	      | InvalidMnemonic mem => "invalid mnemonic " ^ mem
	      | RangeError => "range error"
	      | UnsupportedDirective dir => "unsupported directive " ^ dir
	      | _ => raise exn);
     print "\n");
    
end

