signature TERM =
sig

    datatype t =
	   Number of int
	 | Label of Labels.label
	 | Character of char
	 | Star
    
    val parse : Substring.substring -> (t * Substring.substring) option					  

end
