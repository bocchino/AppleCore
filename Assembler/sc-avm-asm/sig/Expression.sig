(* An operand expression *)

signature EXPRESSION =
sig

    type t

    (* Parse a possible expression from a substring.
       Return NONE if no expression found. *)
    val parse : Substring.substring -> (t * Substring.substring) option

    (* Parse an expression argument, consuming whitespace first.
       Throw an exception if no expression found. *)
    val parseArg : Substring.substring -> t

    (* Parse a list of expressions *)
    val parseList : (Substring.substring -> (t * Substring.substring) option)
		    -> Substring.substring
		    -> (t list * Substring.substring) option

    (* Evaluate an expression, given bindings for labels and for * *)
    val eval : (Label.map * int) -> t -> t
    val evalAsAddr : (Label.map * int) -> t -> int

    (* Report whether an expression represents a zero-page address *)
    val isZeroPage : t -> bool

end

