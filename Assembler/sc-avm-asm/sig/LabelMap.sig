(* A mapping from labels to assembler source lines *)

signature LABEL_MAP =
sig

    (* A source line stores a file, a line number, and an
       address *)
    type source = {file:string,line:int,address:int}
    type map

    (* Create a fresh map *)
    val fresh : map
    (* Add a mapping to the map *)
    val add : (map * Label.t * source) -> map
    (* Look up a label and return the associated address *)
    val lookup :(map * Label.t) -> int option

end
