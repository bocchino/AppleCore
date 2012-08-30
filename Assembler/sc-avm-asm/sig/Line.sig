signature LINE =
sig

    type t

    val parse : File.paths -> File.t -> (t option * File.t) option

end

