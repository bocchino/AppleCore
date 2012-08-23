signature LABELS =
sig

    datatype label =
	     Global of string
	   | Local of int

    val parse : Substring.substring -> (label * Substring.substring) option

end
