signature PARSER =
sig

    type line

    val nextLine : File.paths -> File.t -> (line option * File.t) option
    val parseLine : string -> line option
    val parseFile : File.paths -> string -> unit			      

end

