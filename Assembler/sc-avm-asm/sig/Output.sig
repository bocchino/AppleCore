signature OUTPUT =
sig

    type t

    val new : {dir:string,file:string,origin:int} -> t
    val TF : t * string * int -> t
    val addBytes : t * int list -> t
    val write : t -> unit

end
