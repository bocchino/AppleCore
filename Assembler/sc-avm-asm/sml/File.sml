structure File : FILE =
struct

open Error

type name = string
type paths = string list
type t = paths * (name * int * TextIO.instream) list

fun openIn' [] name =
    raise AssemblyError (FileNotFound name)
  | openIn' (hd::tl) name =
    (name,0,TextIO.openIn (hd ^ "/" ^ name))
    handle IO.Io {...} => openIn' tl name

fun openIn paths name = 
    (paths,[openIn' paths name])

fun includeIn (paths,files) name =
    (paths,(openIn' paths name) :: files)

fun nextLine (paths,[]) = NONE
  | nextLine (paths,(name,n,stream) :: tl) =
    case TextIO.inputLine stream of
	SOME line => SOME (line, (paths,(name,n+1,stream) :: tl))
      | NONE => nextLine (paths,tl)
    
fun name (paths,[]) = "<empty>"
  | name (paths,(str,_,_)::tl) = str

fun lineNum (paths,[]) = 0
  | lineNum (paths,((_,n,_)::tl)) = n

end
