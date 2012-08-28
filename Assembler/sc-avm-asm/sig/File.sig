signature FILE =
sig

    type t
    type paths = string list
    type name = string

    val openIn : paths -> name -> t
    val includeIn : paths -> t -> name -> t
    val nextLine : t -> (string * t) option
    val name : t -> string
    val line : t -> int

end

