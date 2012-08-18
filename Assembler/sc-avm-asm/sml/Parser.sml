structure Parser : PARSER =
  struct

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

  fun normalize num =
      if num < ~65535 orelse num > 65535 then
	  raise RangeError
      else if num < 0 then
	  65536 + num
      else num

  fun parseDigits substr radix =
      case radix of 
	  StringCvt.HEX =>
	  let 
              val (num,substr') = Substring.splitl Char.isHexDigit substr
	      val hex = StringCvt.scanString(Int.scan StringCvt.HEX) 
					    (Substring.string num)
          in 
	      (hex,substr')
	  end
	| StringCvt.DEC =>
	  let
	      val (num,substr') = Substring.splitl Char.isDigit substr
	      val dec = Int.fromString (Substring.string num)
          in
	      (dec,substr')
          end
        | _ => (NONE,substr)

  fun parseNumber substr = 
      case Substring.getc substr of
	  SOME(#"-",substr') =>
          let 
	      val (num,substr'') = parseNumber substr'
          in 
	      case num of
		  NONE   => (num,substr'')
		| SOME n => (SOME (~n),substr'')
	  end
        | SOME(#"$",substr') => 
	  parseDigits substr' StringCvt.HEX 
        | _ =>
	  parseDigits substr StringCvt.DEC

  fun parseLabelDigits substr = 
      case parseDigits substr StringCvt.DEC of 
	  (SOME n,substr') =>
	  if n < 100 then (n,substr')
	  else raise BadLabelError
        | _   => raise BadLabelError

  fun isLabelChar c =
      Char.isAlphaNum c orelse c = #"."

  fun parseLabel substr =
      case Substring.getc substr of
	  NONE               => (NONE,substr)
        | SOME(#".",substr') => 
	  (case parseLabelDigits substr' of
	      (n,substr'') => (SOME (Local n),substr''))
        | SOME(#":",substr') =>
	  (case parseLabelDigits substr' of
	      (n,substr'') => (SOME (Private n),substr''))
        | SOME(c,substr')    =>				
	  if Char.isAlpha c then
	      case Substring.splitl isLabelChar substr of
		  (label,substr'') =>
		  (SOME (Global (Substring.string label)),substr'')
          else
	      raise BadLabelError

  end
