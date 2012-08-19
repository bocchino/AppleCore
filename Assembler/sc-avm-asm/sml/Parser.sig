signature PARSER =
  sig

  exception RangeError
  exception BadLabelError
  exception BadAddressError

  datatype label =
    Global of string
  | Local of int
    
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
  val parseNumber : Substring.substring -> (int * Substring.substring) option
  val parseLabel : Substring.substring -> (label * Substring.substring) option
  val parseTerm : Substring.substring -> (term * Substring.substring) option					  
  val parseExpr : Substring.substring -> (expr * Substring.substring) option

  end

