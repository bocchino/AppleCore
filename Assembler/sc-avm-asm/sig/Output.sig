signature OUTPUT =
sig

    type t

    val new : {dir:string,file:string} -> t
    val tf : t * string -> t
    val addBytes : t * int list -> t
    val write : t -> unit

end
