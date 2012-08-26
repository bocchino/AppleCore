signature PARSER =
sig
    
    type line = (Label.t option) * (Instructions.instruction option)

    val parseLine : string -> line option
    val parseAll : string -> unit			      
end

