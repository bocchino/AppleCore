(* A term of an operand expression *)

signature TERM =
sig

    datatype t =
	   Number of int
	 | Label of Labels.label
	 | Character of char
	 | Star
    
    (* Parse a term from a substring *)	 
    val parse : Substring.substring -> (t * Substring.substring) option					  

    (* Evaluate a term, given bindings for labels and for * *)
    val eval : (LabelMap.map * int) -> t -> t

end
