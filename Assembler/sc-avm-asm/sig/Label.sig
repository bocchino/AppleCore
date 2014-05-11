(* An SC Macro Assembler label.
   Labels may be global or local.
   Private labels are not supported. *)

signature LABEL =
sig

    (* The type of a label *)
    type t

    (* Parse a label from a substring *)
    val parseMain : Substring.substring -> (t * Substring.substring) option
    val parseExpr : Substring.substring -> (t * Substring.substring) option

    (* Type of a mapping from labels to assembler source lines *)
    type map

    (* A source associated with a label *)
    type source = {sourceLine:File.line,
                   address:int}

    (* Create a fresh map *)
    val fresh : map

    (* Add a fresh mapping to the map.  
       Throw an exception if the label is already there. *)
    val add : (map * t * source) -> map

    (* Update the source of a mapping. *)
    val update : (map * t * source) -> map

    (* Look up a label and return the associated address *)
    val lookup :(map * t) -> int option

end
