
(* Printing functions *)

signature PRINTING =
sig

    (* Format a byte as two hex digits *)
    val formatHexByte : int -> string

    (* Format a 16-bit value as four hex digits *)
    val formatHexWord : int -> string

    (* Format an address as hex digits followed by '-' *)
    val formatAddress : int -> string

    (* Print number of blanks corresponding to formatted address *)
    val formatBlankAddress : unit -> string

    (* Format a list of bytes in three columns *)
    val formatBytes : int list -> string

    (* Format a line of assembly output *)
    val formatLine : int option * int list * string -> string

end
