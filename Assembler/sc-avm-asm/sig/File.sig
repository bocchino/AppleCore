
(* An assembler input file *)

signature FILE =
sig

    type t
    type paths = string list
    type name = string

    (* Open file 'name' located in one of 'paths' for input *)
    val openIn : paths -> name -> t

    (* Include file 'name' *)
    val includeIn : t -> name -> t

    (* A type representing a line of an input file *)
    type line

    (* Report the file name that generated a line *)
    val fileName : line -> string

    (* Report the line number from the input file *)
    val lineNumber : line -> int

    (* Provide the string data associated with a line *)
    val data : line -> string

    (* Get the next line from the file *)
    val nextLine : t -> (line * t) option

end

