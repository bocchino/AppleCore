signature LINE =
sig

    type t

    val parse : File.paths * File.t * string -> (t * File.t) option
    val pass1 : File.t * t * int * Label.map -> 
		int * Label.map * Instruction.t option

end

