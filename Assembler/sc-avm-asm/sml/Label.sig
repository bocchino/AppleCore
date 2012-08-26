(* An SC Macro Assembler label.
   Labels may be global or local.
   Private labels are not supported. *)

signature LABEL =
sig

    datatype t =
	     (* A global label, such as FOO *)
	     Global of string
             (* A local label, such as .12 *)
	   | Local of int

    (* Parse a label from a substring *)
    val parse : Substring.substring -> (t * Substring.substring) option

end
