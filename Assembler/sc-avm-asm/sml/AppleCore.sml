structure AppleCore : APPLECORE =
struct

open Error

datatype size =
	 Signed of int
       | Unsigned of int

datatype constant =
	 Label of Label.t
       | Literal of IntInf.int
		     
datatype instruction =
	 BRF of Expression.t
       | BRU of Expression.t
       | CFD of Expression.t
       | CFI
       | ADD of int
       | ANL of int
       | DCR of int
       | DSP of int
       | ICR of int
       | ISP of int
       | MTS of int
       | MTV of int * Expression.t
       | NEG of int
       | NOT of int
       | ORL of int
       | ORX of int
       | PHC of constant
       | PVA of int
       | RAF of int
       | SHL of int
       | STM of int
       | SUB of int
       | TEQ of int
       | VTM of int * Expression.t
       | DIV of size
       | EXT of size
       | MUL of size
       | SHR of size
       | TGE of size
       | TGT of size
       | TLE of size
       | TLT of size
		
fun parseUnsigned substr =
    Numbers.normalize 256 (Numbers.parseArg substr)
    
fun parseSigned substr =
    case Numbers.parse (Substring.dropl Char.isSpace substr) of
	SOME (num,rest) =>
	let
	    val num' = Numbers.normalize 256 num
	in
	    case Substring.getc rest of
		SOME (c,_) => if (Char.toUpper c) = #"S" 
			      then Signed num'
			      else Unsigned num'
              | NONE => Unsigned num'
	end
      | _ => raise BadAddress

fun parseConstant substr =
    let
        val constant = (Substring.dropl Char.isSpace substr)
    in
	case Numbers.parse constant of
	    SOME (n,_) => Literal n
	  | NONE => (case Label.parse constant of
			 SOME (l,_) => Label l
		       | NONE => raise BadAddress)
    end
		   
fun parseMV (instr,delim) substr =
    case Numbers.parse (Substring.dropl Char.isSpace substr) of
	SOME (var,rest) => 
	let
	    val (d,rest') = Substring.splitAt(Substring.dropl Char.isSpace rest,2) 
		handle Subscript => raise BadAddress
        in
	    if (Substring.string d) = delim
	    then instr (Numbers.normalize 256 var,Expression.parseArg rest')
            else raise BadAddress
        end
      | _ => raise BadAddress
		   
		   
fun parse substr =
    let
	val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 
    in
	case (Substring.translate (Char.toString o Char.toUpper) mem) of
	    "BRF" => SOME (BRF (Expression.parseArg rest))
	  | "BRU" => SOME (BRU (Expression.parseArg rest))
	  | "CFD" => SOME (CFD (Expression.parseArg rest))
          | "CFI" => SOME CFI  
          | "ADD" => SOME (ADD (parseUnsigned rest))
          | "ANL" => SOME (ANL (parseUnsigned rest))
          | "DCR" => SOME (DCR (parseUnsigned rest))
	  | "DSP" => SOME (DSP (parseUnsigned rest))
	  | "ICR" => SOME (ICR (parseUnsigned rest))
          | "ISP" => SOME (ISP (parseUnsigned rest))
          | "MTS" => SOME (MTS (parseUnsigned rest))
          | "MTV" => SOME (parseMV (MTV,"<-") rest)
	  | "NEG" => SOME (NEG (parseUnsigned rest))
	  | "NOT" => SOME (NOT (parseUnsigned rest))
	  | "ORL" => SOME (ORL (parseUnsigned rest))
	  | "ORX" => SOME (ORX (parseUnsigned rest))
	  | "PHC" => SOME (PHC (parseConstant rest))
	  | "PVA" => SOME (PVA (parseUnsigned rest))
	  | "RAF" => SOME (RAF (parseUnsigned rest))
	  | "SHL" => SOME (SHL (parseUnsigned rest))
	  | "STM" => SOME (STM (parseUnsigned rest))
	  | "SUB" => SOME (SUB (parseUnsigned rest))
	  | "TEQ" => SOME (TEQ (parseUnsigned rest))
          | "VTM" => SOME (parseMV (VTM,"->") rest)
	  | "DIV" => SOME (DIV (parseSigned rest))
	  | "EXT" => SOME (EXT (parseSigned rest))
	  | "MUL" => SOME (MUL (parseSigned rest))
	  | "SHR" => SOME (SHR (parseSigned rest))
	  | "TGE" => SOME (TGE (parseSigned rest))
	  | "TGT" => SOME (TGT (parseSigned rest))
	  | "TLE" => SOME (TLE (parseSigned rest))
	  | "TLT" => SOME (TLT (parseSigned rest))
          | _ => NONE
    end
    
end
