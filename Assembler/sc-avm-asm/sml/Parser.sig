signature PARSER =
sig
    
    type line = (Labels.label option) * (Instructions.instruction option)

    val parseLine : string -> line option
    val parseAll : string -> unit			      
end

