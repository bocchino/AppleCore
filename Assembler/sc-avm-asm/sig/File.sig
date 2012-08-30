signature FILE =
sig

    type t
    type paths = string list
    type name = string

    val openIn : paths -> name -> t
    val includeIn : t -> name -> t

    type line

    val fileName : line -> string
    val lineNumber : line -> int
    val data : line -> string
    val nextLine : t -> (line * t) option

end

