signature LINE =
sig

    type t

    val parse : File.t * File.line -> t * File.t
    val pass1 : File.line * t * int * Label.map -> 
		int * Label.map * Instruction.t option

end

