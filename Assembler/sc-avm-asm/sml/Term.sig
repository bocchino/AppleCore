(* A term of an operand expression *)

signature TERM =
sig

    datatype t =
	   (* A concrete address *)
	   Number of int
	   (* A symbolic address *)
	 | Label of Label.t
	   (* A character such as 'A *)
	 | Character of char
  	   (* A star indicating the current address *)
	 | Star
    
    (* Parse a term from a substring *)	 
    val parse : Substring.substring -> (t * Substring.substring) option					  

    (* Evaluate a term, given bindings for labels and for * *)
    val eval : (LabelMap.map * int) -> t -> t

end
