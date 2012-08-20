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

  fun parseDelimArg substr =
      case Substring.getc (Substring.dropl Char.isSpace substr) of
	  SOME (c,substr') =>
	  let 
	      val (str,rest) = Substring.splitl (fn c' => not (c'=c)) substr'
          in
	      case Substring.getc rest of
		  SOME (c,_) => Substring.string str
                | _          => raise Parser.BadAddressError
          end
        | _ => raise Parser.BadAddressError

  fun parseExprList substr =
      case Parser.parseList Parser.parseExpr substr of
          SOME ([],_)  => raise Parser.BadAddressError
	| SOME (lst,_) => lst
        | NONE         => raise Parser.BadAddressError

  fun parseHexString substr =
      let
	  fun getHexNum substr =
	      let
		  val (digits,_) = Substring.splitAt(substr,2)
		      handle Subscript => raise Parser.BadAddressError
              in
		  case StringCvt.scanString(Int.scan StringCvt.HEX) 
					   (Substring.string digits) of
		      SOME n => n
	            | NONE   => raise Parser.BadAddressError
              end
	  fun parseHexString' results substr =
	      case Substring.first substr of
		  NONE   => List.rev results
                | SOME c =>
		  if Char.isSpace c then 
		      List.rev results
		  else
		      let 
			  val num = getHexNum substr
		      in
			  parseHexString' (num :: results) (Substring.triml 2 substr)
		      end
      in
	  case parseHexString' [] (Substring.dropl Char.isSpace substr) of
	      []      => raise Parser.BadAddressError
	    | results => results
      end

  fun parseDirective substr =
      let
	  val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 
      in
	  case Substring.string mem of
	      ".AS" => SOME (AS (parseDelimArg rest))
	    | ".AT" => SOME (AT (parseDelimArg rest))
	    | ".BS" => SOME (BS (Parser.parseExprArg rest))
	    | ".DA" => SOME (DA (parseExprList rest))
            | ".EQ" => SOME (EQ (Parser.parseExprArg rest))
	    | ".HS" => SOME (HS (parseHexString rest))
            | ".IN" => SOME (IN (parseStringArg rest))
            | ".OR" => SOME (OR (Parser.parseExprArg rest))
	    | ".TF" => SOME (TF (parseStringArg rest))
            | _ => NONE
      end

end
