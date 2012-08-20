signature LABELS =
sig

    exception BadLabelError

    datatype label =
	     Global of string
	   | Local of int
    
    val parse : Substring.substring -> (label * Substring.substring) option

end
