signature PARSER =
sig
    
    type line = (Label.t option) * (Instruction.t option)

    val parseLine : string -> line option
    val parseAll : string -> unit			      
end

