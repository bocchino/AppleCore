structure Parsing : PARSING =
struct

open Char
open Error
open Label
open Substring

fun parseListRest parse results substr =
    case getc (dropl isSpace substr) of
        SOME (#",",substr') =>
        (case parse (dropl isSpace substr') of
            SOME (result, substr'') => 
            parseListRest parse (result :: results) substr''
          | _ => raise AssemblyError BadAddress)
      | _ => SOME (results,substr)
             
fun parseList parse substr =
    case parse (dropl isSpace substr) of
        SOME (result,substr') => parseListRest parse [result] substr'
      | _ => SOME ([],substr)

end
