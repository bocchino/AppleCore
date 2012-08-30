signature INSTRUCTION =
sig

    type t
			
    (* Parse an instruction from a substring *)
    val parse : Substring.substring -> t option

    (* If t is an .IN directive, then include the file *)				       
    val includeIn : t -> File.paths * File.t -> File.t

    (* Compute a new instruction, address, and label map *)
    val pass1 : Label.t option * t -> Label.source * Label.map -> t * int * Label.map

end
