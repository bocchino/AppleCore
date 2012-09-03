signature LINE =
sig

    type t

    val parse : File.t * File.line -> t * File.t
    val pass1 : t * int * Label.map -> t * int * Label.map
    val pass2 : Output.t * t * int * Label.map -> Output.t * string

end

