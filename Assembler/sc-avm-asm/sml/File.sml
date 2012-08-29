structure File : FILE =
struct

open Error

type t = (string * int * TextIO.instream) list
type paths = string list
type name = string

fun openIn' [] name =
    raise AssemblyError (FileNotFound name)
  | openIn' (hd::tl) name =
    (name,0,TextIO.openIn (hd ^ "/" ^ name))
    handle IO.Io {...} => openIn' tl name

fun openIn paths name = 
    [openIn' paths name]

fun includeIn paths file name =
    (openIn' paths name) :: file

fun nextLine [] = NONE
  | nextLine ((name,n,stream) :: tl) =
    case TextIO.inputLine stream of
	SOME line => SOME (line, (name,n+1,stream) :: tl)
      | NONE => nextLine tl
    
fun name [] = "<empty>"
  | name ((str,_,_)::tl) = str

fun line [] = 0
  | line ((_,n,_)::tl) = n

end
