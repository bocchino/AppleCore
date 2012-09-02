signature PRINTING =
sig

    val formatHexByte : int -> string
    val formatHexWord : int -> string
    val formatAddress : int -> string
    val formatBlankAddress : unit -> string
    val formatBytes : int list -> string
    val formatLine : int option * int list * string -> string

end
