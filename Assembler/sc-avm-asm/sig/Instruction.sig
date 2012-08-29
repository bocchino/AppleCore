signature INSTRUCTION =
sig

    type t
			
    (* Parse an instruction from a substring *)
    val parse : Substring.substring -> t option

    (* If t is an .IN directive, then include the file *)				       
    val includeIn : t -> File.paths * File.t -> File.t

    (* Compute a new address and label map for an instruction *)
    val pass1 : t -> Label.t option * Label.source * Label.map -> int * Label.map

end
