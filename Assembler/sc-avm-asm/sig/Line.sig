signature LINE =
sig

    type t

    val pass1 : File.paths * File.t * string * int * Label.map -> 
		(File.t * int * Label.map * Instruction.t option) option

end

