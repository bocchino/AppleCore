(* An SC Macro Assembler label.
   Labels may be global or local.
   Private labels are not supported. *)

signature LABEL =
sig

    (* The type of a label *)
    type t

    (* Parse a label from a substring *)
    val parse : Substring.substring -> (t * Substring.substring) option

    (* Type of a mapping from labels to assembler source lines *)
    type map

    (* Type of a source line *)
    type source = {file:string,line:int,address:int}

    (* Create a fresh map *)
    val fresh : map

    (* Add a mapping to the map *)
    val add : (map * t * source) -> map

    (* Look up a label and return the associated address *)
    val lookup :(map * t) -> int option

end
