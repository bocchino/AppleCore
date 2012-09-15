
(* Module for assembler output *)

signature OUTPUT =
sig

    type t

    (* Create a new output representation.
       Output consists of a directory, file, and origin.  *)
    val new : {dir:string,file:string,origin:int} -> t

    (* Record the effect of a .TF directive *)
    val TF : t * string * int -> t

    (* Add a list of bytes to the output *)
    val addBytes : t * int list -> t

    (* Write the output to the file recorded in t *)
    val write : t -> unit

end
