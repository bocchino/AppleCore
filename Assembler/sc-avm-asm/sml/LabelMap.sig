signature LABEL_MAP =
sig

    type source = {file:string,line:int,address:int}
    type map

    val fresh : map
    val add : (map * Labels.label * source) -> map
    val lookup :(map * Labels.label) -> int option

end
