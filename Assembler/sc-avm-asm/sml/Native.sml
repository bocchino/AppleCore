structure Native : NATIVE =
struct

open Error

datatype operand =
	 None
       | ImmediateLow of Expression.t
       | ImmediateHigh of Expression.t
       | Direct of Expression.t
       | DirectX of Expression.t
       | DirectY of Expression.t
       | Indirect of Expression.t
       | IndirectX of Expression.t
       | IndirectY of Expression.t
		      
datatype mnemonic =
	 ADC
       | AND
       | ASL
       | BCC
       | BCS
       | BEQ
       | BIT
       | BMI
       | BNE
       | BPL
       | BRK
       | BVC
       | BVS
       | CLC
       | CLD
       | CLI
       | CLV
       | CMP
       | CPX
       | CPY
       | DEC
       | DEX
       | DEY
       | EOR
       | INC
       | INX
       | INY
       | JMP
       | JSR
       | LDA
       | LDX
       | LDY
       | LSR
       | NOP
       | ORA
       | PHA
       | PHP
       | PLA
       | PLP
       | ROL
       | ROR
       | RTI
       | RTS
       | SBC
       | SEC
       | SED
       | SEI
       | STA
       | STX
       | STY
       | TAX
       | TAY
       | TSX
       | TXA
       | TXS
       | TYA
	 
datatype instruction =
	 Instruction of mnemonic * operand
			
fun getMnemonic substr =
    case (Substring.translate (Char.toString o Char.toUpper) substr) of
	"ADC" => SOME ADC
      | "AND" => SOME AND
      | "ASL" => SOME ASL
      | "BCC" => SOME BCC
      | "BCS" => SOME BCS
      | "BEQ" => SOME BEQ
      | "BIT" => SOME BIT
      | "BMI" => SOME BMI
      | "BNE" => SOME BNE
      | "BPL" => SOME BPL
      | "BRK" => SOME BRK
      | "BVC" => SOME BVC
      | "BVS" => SOME BVS
      | "CLC" => SOME CLC 
      | "CLD" => SOME CLD
      | "CLI" => SOME CLI
      | "CLV" => SOME CLV
      | "CMP" => SOME CMP
      | "CPX" => SOME CPX
      | "CPY" => SOME CPY
      | "DEC" => SOME DEC
      | "DEX" => SOME DEX
      | "DEY" => SOME DEY
      | "EOR" => SOME EOR
      | "INC" => SOME INC
      | "INX" => SOME INX
      | "INY" => SOME INY
      | "JMP" => SOME JMP
      | "JSR" => SOME JSR
      | "LDA" => SOME LDA
      | "LDX" => SOME LDX
      | "LDY" => SOME LDY
      | "LSR" => SOME LSR
      | "NOP" => SOME NOP
      | "ORA" => SOME ORA
      | "PHA" => SOME PHA
      | "PHP" => SOME PHP
      | "PLA" => SOME PLA
      | "PLP" => SOME PLP
      | "ROL" => SOME ROL
      | "ROR" => SOME ROR
      | "RTI" => SOME RTI
      | "RTS" => SOME RTS
      | "SBC" => SOME SBC
      | "SEC" => SOME SEC
      | "SED" => SOME SED
      | "SEI" => SOME SEI
      | "STA" => SOME STA
      | "STX" => SOME STX
      | "STY" => SOME STY
      | "TAX" => SOME TAX
      | "TAY" => SOME TAY
      | "TSX" => SOME TSX
      | "TXA" => SOME TXA
      | "TXS" => SOME TXS
      | "TYA" => SOME TYA
      | _     => NONE
		 
fun parseOperand substr =
    case Substring.getc substr of
	NONE               => NONE
      | SOME(#"#",substr') => 
	(case Expression.parse substr' of
	     SOME (e,substr'') => SOME (ImmediateLow e,substr'')
	   | _                 => raise BadAddress)
      | SOME(#"/",substr') => 
	(case Expression.parse substr' of
	     SOME (e,substr'') => SOME (ImmediateHigh e,substr'')
           | _                 => raise BadAddress)
      | SOME(#"(",substr') => 
	(case Expression.parse substr' of
	     SOME (e,substr'') =>
	     if Substring.isPrefix ",X)" substr'' then
		 SOME (IndirectX e,Substring.triml 3 substr'')
	     else if Substring.isPrefix "),Y" substr'' then
		 SOME (IndirectY e,Substring.triml 3 substr'')
	     else if Substring.isPrefix ")" substr'' then
		 SOME (Indirect e,Substring.triml 1 substr'')
	     else raise BadAddress
	   | _ => raise BadAddress)
      | _ => 
	(case Expression.parse substr of
	     SOME (e,substr'') => SOME (Direct e,substr'')
	   | _ => raise BadAddress)
	     
fun parse substr =
    let
	val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 	  		   
    in
	case getMnemonic mem of
	    NONE      => NONE
	  | SOME mem' => 
	    let
		val (spc,arg) = Substring.splitl Char.isSpace rest
            in
		if ((Substring.string spc) = " ") then
		    case parseOperand arg of
 			SOME (oper,_) =>
			SOME (Instruction(mem',oper))
		      | _  => 
			SOME (Instruction(mem',None))
                else SOME (Instruction(mem',None))
	    end
    end
    
end
