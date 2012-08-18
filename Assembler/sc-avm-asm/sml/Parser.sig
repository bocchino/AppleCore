signature PARSER =
  sig

  exception RangeError
  exception BadLabelError
  exception BadAddressError

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
    Term of term
  | Add of term * expr
  | Sub of term * expr
  | Mul of term * expr
  | Div of term * expr

  val normalize : int -> int
  val parseNumber : Substring.substring -> int option * Substring.substring
  val parseLabel : Substring.substring -> label option * Substring.substring
  val parseTerm : Substring.substring -> term option * Substring.substring					  
  val parseExpr : Substring.substring -> expr option * Substring.substring

  end

