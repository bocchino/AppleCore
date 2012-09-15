
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

    (* Evaluate an expression, given bindings for * and for labels *)
    val eval : int * Label.map -> t -> t

    (* Evaluate an expression to an address
       Raises UndefinedLabel if the expression can't be fully evaluated *)
    val evalAsAddr : int * Label.map -> t -> int

    (* Report whether an expression represents a zero-page address *)
    val isZeroPage : t -> bool

end

