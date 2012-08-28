structure Error : ERROR =
struct

exception BadAddress
exception BadLabel
exception FileNotFound of string
exception InvalidMnemonic of string
exception RangeError
exception UndefinedLabel
exception UnsupportedDirective of string
	  
fun show line n e = (
    print ("line " ^ (Int.toString n) ^ ": " ^ 
	   (case e of 
		BadAddress => "bad address"
	      | BadLabel => "bad label"
	      | InvalidMnemonic mem => "invalid mnemonic " ^ mem
	      | RangeError => "range error"
	      | UnsupportedDirective dir => "unsupported directive " ^ dir
	      | _ => raise e) ^ "\n");
    print line
)
    
end
