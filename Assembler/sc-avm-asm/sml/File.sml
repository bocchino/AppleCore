structure File : FILE =
struct

open Error

type t = int * (TextIO.instream list)
type paths = string list
type name = string

fun openIn' [] name =
    raise FileNotFound name
  | openIn' (hd::tl) name =
    TextIO.openIn (hd ^ "/" ^ name)
    handle IO.Io {...} => openIn' tl name

fun openIn paths name = 
    (0,[openIn' paths name])

fun includeIn paths (n,streams) name =
    (n,((openIn' paths name) :: streams))

fun nextLine (n,[]) = NONE
  | nextLine (n,(hd :: tl)) =
    case TextIO.inputLine hd of
	SOME line => SOME (line, (n+1,hd :: tl))
      | NONE => nextLine (n,tl)
    
fun lineNumber (n,_) = n

end
