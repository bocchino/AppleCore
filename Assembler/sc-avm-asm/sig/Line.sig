
(* A line to assemble *)

signature LINE =
sig

    type t

    (* Parse a line from the input file *)
    val parse : File.t * File.line -> t * File.t

    (* Do pass 1 *)
    val pass1 : t * int * Label.map -> t * int * Label.map

    (* Do pass 2 *)
    val pass2 : Output.t * t * int * Label.map -> Output.t * string

end

