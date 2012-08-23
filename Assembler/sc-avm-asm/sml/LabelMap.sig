signature LABEL_MAP =
sig

    type source = {file:string,line:int,address:int}
    type map;

    val new : map
    val add : (map * Labels.label * source) -> map

end
