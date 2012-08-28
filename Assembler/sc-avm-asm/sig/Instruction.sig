signature INSTRUCTION =
sig

    type t
			
    val parse : Substring.substring -> t option
    val includeIn : File.paths -> File.t -> t -> File.t

end
