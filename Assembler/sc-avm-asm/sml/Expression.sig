signature EXPRESSION =
sig

    datatype t =
	     Term of Term.t
	   | Add of Term.t * t
	   | Sub of Term.t * t
	   | Mul of Term.t * t
	   | Div of Term.t * t

    val parse : Substring.substring -> (t * Substring.substring) option
    val parseArg : Substring.substring -> t
					      
    val parseList : (Substring.substring -> ('a * Substring.substring) option)
		    -> Substring.substring
		    -> ('a list * Substring.substring) option

end

