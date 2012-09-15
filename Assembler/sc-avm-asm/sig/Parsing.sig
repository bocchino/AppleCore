(* Parsing functions *)

signature PARSING =
sig

    (* Parse a list of items *)
    val parseList : (Substring.substring -> 
		     ('a * Substring.substring) option) ->
		    Substring.substring ->
		    ('a list * Substring.substring) option

end

