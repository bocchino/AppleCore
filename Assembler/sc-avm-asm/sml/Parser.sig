signature PARSER =
  sig

  exception RangeError
  exception BadLabelError

  datatype label =
    Global of string
  | Local of int
  | Private of int
    
  datatype term =
    Number of int
  | Label of label
  | Character of char
  | Star

  datatype expr =
    term
  | Add of expr * term
  | Sub of expr * term
  | Mul of expr * term
  | Div of expr * term

  val normalize : int -> int
  val parseNumber : Substring.substring -> int option * Substring.substring
  val parseLabel : Substring.substring -> label option * Substring.substring

  end;

