structure Directives : DIRECTIVES =
  struct

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

  fun parseStringArg substr =
      case Substring.string (Substring.dropl Char.isSpace substr) of
	  ""  => raise Parser.BadAddressError
        | str => str

  fun parseExprArg substr =
      case Parser.parseExpr (Substring.dropl Char.isSpace substr) of
	  (SOME e,substr') => e
	| _                => raise Parser.BadAddressError

  fun parseDirective substr =
      let
	  val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 
      in
	  case Substring.string mem of
(*
	      ".AS" => SOME (AS (parseStringArg rest))
	    | ".AT" => SOME (AT (parseStringArg rest))
*)
	      ".BS" => SOME (BS (parseExprArg rest))
(*
	    | ".DA" => parseDA rest
*)
            | ".EQ" => SOME (EQ (parseExprArg rest))
(*
	    | ".HS" => parseHS rest
*)
            | ".IN" => SOME (IN (parseStringArg rest))
            | ".OR" => SOME (OR (parseExprArg rest))
	    | ".TF" => SOME (TF (parseStringArg rest))
            | _ => NONE
      end

end
