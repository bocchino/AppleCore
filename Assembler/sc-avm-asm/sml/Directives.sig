signature DIRECTIVES =
  sig

  datatype directive =
    AS of string
  | AT of string
  | BS of Parser.expr
  | DA of Parser.expr list
  | EQ of Parser.expr
  | HS of int list   
  | IN of string
  | OR of Parser.expr
  | TF of string

  val parseDirective : Substring.substring -> directive option

  end
