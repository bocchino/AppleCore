structure AppleCore : APPLECORE =
struct

datatype size =
	 Signed of int
       | Unsigned of int
		     
datatype instruction =
	 BRF of Operands.expr
       | BRU of Operands.expr
       | CFD of Operands.expr
       | CFI
       | ADD of int
       | ANL of int
       | DCR of int
       | DSP of int
       | ICR of int
       | ISP of int
       | MTS of int
       | MTV of int * Operands.expr
       | NEG of int
       | NOT of int
       | ORL of int
       | ORX of int
       | PHC of int
       | PVA of int
       | RAF of int
       | SHL of int
       | STM of int
       | SUB of int
       | TEQ of int
       | VTM of int * Operands.expr
       | DIV of size
       | EXT of size
       | MUL of size
       | SHR of size
       | TGE of size
       | TGT of size
       | TLE of size
       | TLT of size
		
fun parseUnsigned substr =
    Numbers.normalize 256 (Operands.parseNumberArg substr)
    
fun parseSigned substr =
    case Numbers.parseNumber (Substring.dropl Char.isSpace substr) of
	SOME (num,rest) => (
	case Substring.getc rest of
	    SOME (c,_) => if (Char.toUpper c) = #"S" 
			  then Signed num
			  else Unsigned num
          | NONE => Unsigned num
	)	      
      | _ => raise Operands.BadAddressError
		   
fun parseMV (instr,delim) substr =
    case Numbers.parseNumber (Substring.dropl Char.isSpace substr) of
	SOME (var,rest) => 
	let
	    val (d,rest') = Substring.splitAt(Substring.dropl Char.isSpace rest,2) 
		handle Subscript => raise Operands.BadAddressError
        in
	    if (Substring.string d)=delim
	    then instr (var,Operands.parseExprArg rest')
            else raise Operands.BadAddressError
        end
      | _ => raise Operands.BadAddressError
		   
		   
fun parse substr =
    let
	val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 
    in
	case (Substring.translate (Char.toString o Char.toUpper) mem) of
	    "BRF" => SOME (BRF (Operands.parseExprArg rest))
	  | "BRU" => SOME (BRU (Operands.parseExprArg rest))
	  | "CFD" => SOME (CFD (Operands.parseExprArg rest))
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
	  | "PHC" => SOME (PHC (parseUnsigned rest))
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
