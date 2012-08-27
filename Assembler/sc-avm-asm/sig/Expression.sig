(* An operand expression *)

signature EXPRESSION =
sig

    (* An expression is a single term or a binary operation
       comprising a term and an expression *)
    datatype t =
	     Term of Term.t
	   | Add of Term.t * t
	   | Sub of Term.t * t
	   | Mul of Term.t * t
	   | Div of Term.t * t

    (* Parse a possible expression from a substring.
       Return NONE if no expression found. *)
    val parse : Substring.substring -> (t * Substring.substring) option

    (* Parse an expression argument, consuming whitespace first.
       Throw an exception if no expression found. *)
    val parseArg : Substring.substring -> t

    (* Parse a list of items *)
    val parseList : (Substring.substring -> ('a * Substring.substring) option)
		    -> Substring.substring
		    -> ('a list * Substring.substring) option

    (* Evaluate an expression, given bindings for labels and for * *)
    val eval : (LabelMap.map * int) -> t -> t
    val evalAsAddr : (LabelMap.map * int) -> t -> int

    (* Report whether an expression represents a zero-page address *)
    val isZeroPage : t -> bool

end

