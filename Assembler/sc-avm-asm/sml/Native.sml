structure Native : INSTRUCTION =
struct

open Error

datatype operand =
         Absolute of Expression.t
       | AbsoluteX of Expression.t
       | AbsoluteY of Expression.t
       | ImmediateLow of Expression.t
       | ImmediateHigh of Expression.t
       | Implied
       | Indirect of Expression.t
       | IndirectX of Expression.t
       | IndirectY of Expression.t
       | Relative of Expression.t
       | ZeroPage of Expression.t
       | ZeroPageX of Expression.t
       | ZeroPageY of Expression.t
		      
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
	 
type t = mnemonic * operand

type opcode = int

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

fun parseRelative operand =
    case Expression.parse operand of
	SOME (e,rest) => SOME (Relative e,rest)
      | _ => raise AssemblyError BadAddress

fun parseNonRelative (mnemonic,operand) =		 
    case Substring.getc operand of
	NONE               => NONE
      | SOME(#"#",substr') => 
	(case Expression.parse substr' of
	     SOME (e,substr'') => SOME (ImmediateLow e,substr'')
	   | _                 => raise AssemblyError BadAddress)
      | SOME(#"/",substr') => 
	(case Expression.parse substr' of
	     SOME (e,substr'') => SOME (ImmediateHigh e,substr'')
           | _                 => raise AssemblyError BadAddress)
      | SOME(#"(",substr') => 
	(case Expression.parse substr' of
	     SOME (e,substr'') =>
	     if Substring.isPrefix ",X)" substr'' then
		 SOME (IndirectX e,Substring.triml 3 substr'')
	     else if Substring.isPrefix "),Y" substr'' then
		 SOME (IndirectY e,Substring.triml 3 substr'')
	     else if Substring.isPrefix ")" substr'' then
		 SOME (Indirect e,Substring.triml 1 substr'')
	     else raise AssemblyError BadAddress
	   | _ => raise AssemblyError BadAddress)
      | _ => 
	(case Expression.parse operand of
	     SOME (e,substr'') => if Substring.isPrefix ",X" substr'' then
				      SOME (AbsoluteX e,Substring.triml 2 substr'')
				  else if Substring.isPrefix ",Y" substr'' then
				      SOME (AbsoluteY e,Substring.triml 2 substr'')
				  else SOME (Absolute e,substr'')
	   | _ => raise AssemblyError BadAddress)



fun parseOperand (mnemonic,operand) =
    case mnemonic of
	BCC => parseRelative operand
      | BCS => parseRelative operand
      | BEQ => parseRelative operand
      | BMI => parseRelative operand
      | BNE => parseRelative operand
      | BPL => parseRelative operand
      | BVC => parseRelative operand
      | BVS => parseRelative operand
      | _ => parseNonRelative (mnemonic,operand)

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
		    case parseOperand (mem',arg) of
 			SOME (oper,_) =>
			SOME (mem',oper)
		      | _  => SOME (mem',Implied)
                else SOME (mem',Implied)
	    end
    end

fun includeIn inst file = file

fun pass1_inst (address,map) operand = 
    let
	fun result zp nzp expr =
	    let
		val expr = Expression.eval (address,map) expr
	    in
		if Expression.isZeroPage expr
		then zp expr
		else nzp expr
	    end
    in
    case operand of
	Absolute expr => result ZeroPage Absolute expr
      | AbsoluteX expr => result ZeroPageX AbsoluteX expr
      | AbsoluteY expr => result ZeroPageY AbsoluteY expr
      | _ => operand
    end

fun pass1_size operand = case operand of
			     Absolute _ => 3
			   | AbsoluteX _ => 3
			   | AbsoluteY _ => 3
			   | Implied => 1
			   | Indirect _ => 3
			   | _ => 2

fun pass1 (label,(mnemonic,operand)) ({sourceLine,address},map) =
    let
	val operand = pass1_inst (address,map) operand
	val size = pass1_size operand
    in
	((mnemonic,operand),address + size,map)
    end

end
