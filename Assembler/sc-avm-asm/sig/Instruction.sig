
(* An instruction to assemble *)

signature INSTRUCTION =
sig

    type t
			
    (* Parse an instruction *)
    val parse : Substring.substring -> t option

    (* If t is an .IN directive, then include the file *)    
    val includeIn : t -> File.t -> File.t

    (* Do pass 1 *)
    val pass1 : Label.t option * t -> 
		Label.source * Label.map -> 
		t * int * Label.map

    (* Do pass 2 *)
    val pass2 : Output.t * File.line * t * int * Label.map -> 
		Output.t * string

end
