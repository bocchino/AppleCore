structure Native : INSTRUCTION =
struct

open Error

datatype single =
	 Low of Expression.t
       | High of Expression.t

datatype operand =
         Absolute of Expression.t
       | AbsoluteX of Expression.t
       | AbsoluteY of Expression.t
       | Immediate of single
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
	     SOME (e,substr'') => SOME (Immediate (Low e),substr'')
	   | _                 => raise AssemblyError BadAddress)
      | SOME(#"/",substr') => 
	(case Expression.parse substr' of
	     SOME (e,substr'') => SOME (Immediate (High e),substr'')
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

fun pass1_inst (address,map) (mnemonic,operand) = 
    let
	fun result zp expr = let
	    val expr = Expression.eval (address,map) expr
	in
	    if Expression.isZeroPage expr
	    then zp expr
	    else operand
	end
    in
	case (mnemonic,operand) of
	    (* These instructions have an Absolute, Y mode *)
	    (* but no Zero Page, Y mode.  So don't convert *)
	    (* them from Absolute,Y to Zero Page, Y. *)
	    (ADC,AbsoluteY _) => operand
	  | (AND,AbsoluteY _) => operand
	  | (CMP,AbsoluteY _) => operand
	  | (EOR,AbsoluteY _) => operand
	  | (LDA,AbsoluteY _) => operand
	  | (ORA,AbsoluteY _) => operand
	  | (SBC,AbsoluteY _) => operand
	  | (STA,AbsoluteY _) => operand
	  (* Otherwise, convert from absolute to zero page if possible. *)
	  | (_,Absolute expr) => result ZeroPage expr
	  | (_,AbsoluteX expr) => result ZeroPageX expr
	  | (_,AbsoluteY expr) => result ZeroPageY expr
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
	val operand = pass1_inst (address,map) (mnemonic,operand)
	val size = pass1_size operand
    in
	((mnemonic,operand),address + size,map)
    end

fun instBytes (inst,addr,map) = 
    let
	fun bytes expr =
	    Numbers.bytes (Expression.evalAsAddr (addr,map) expr)
	fun lowByte expr =
	    Numbers.lowByte (Expression.evalAsAddr (addr,map) expr)
	fun highByte expr =
	    Numbers.highByte (Expression.evalAsAddr (addr,map) expr)
	fun singleByte single =
	    case single of
		Low expr => lowByte expr
	      | High expr => highByte expr
	fun relativeAddress expr =
	    let
		val target = Expression.evalAsAddr (addr,map) expr
		val offset = Numbers.normalize 65536 (IntInf.fromInt (target - addr - 2))
	    in
		if offset >= 0x80 andalso offset <= 0xFF80 then
		    raise AssemblyError RangeError
		else 
		    Numbers.lowByte offset
	    end
	fun operandBytes opcode operand =
	    case operand of 
		Absolute expr => opcode :: (bytes expr)
	      | AbsoluteX expr => opcode :: (bytes expr)
	      | AbsoluteY expr => opcode :: (bytes expr)
	      | Immediate single => [opcode,singleByte single]
	      | Implied => [opcode]
	      | Indirect expr => opcode :: (bytes expr)
	      | IndirectX expr => [opcode,lowByte expr]
	      | IndirectY expr => [opcode,lowByte expr]
	      | Relative expr => [opcode,relativeAddress expr]
	      | ZeroPage expr => [opcode,lowByte expr]
	      | ZeroPageX expr => [opcode,lowByte expr]
	      | ZeroPageY expr => [opcode,lowByte expr]
	fun modes (opcodes:int * int * int * int * int * int * int * int) operand =
	    case operand of
		Immediate _ => operandBytes (#1 opcodes) operand
	      | ZeroPage expr => operandBytes (#2 opcodes) operand
	      | ZeroPageX expr => operandBytes (#3 opcodes) operand
	      | Absolute expr => operandBytes (#4 opcodes) operand
	      | AbsoluteX expr => operandBytes (#5 opcodes) operand
	      | AbsoluteY expr => operandBytes (#6 opcodes) operand
	      | IndirectX expr => operandBytes (#7 opcodes) operand
	      | IndirectY expr => operandBytes (#8 opcodes) operand
	      | _ => raise AssemblyError BadAddress
	fun branch opcode expr =
	    operandBytes opcode (Relative expr)
	fun rotate (opcodes:int * int * int * int * int) operand =
	    let val opcode = case operand of
				 Implied => #1 opcodes
			       | ZeroPage expr => #2 opcodes
			       | ZeroPageX expr => #3 opcodes
			       | Absolute expr => #4 opcodes
			       | AbsoluteX expr => #5 opcodes
			       | _ => raise AssemblyError BadAddress
	    in
		operandBytes opcode operand
	    end
    in
	case inst of
	    (ADC,operand) => modes (0x69,0x65,0x75,0x6D,0x7D,0x79,0x61,0x71) operand
	  | (AND,operand) => modes (0x29,0x25,0x35,0x2D,0x3D,0x39,0x21,0x31) operand
	  | (ASL,Implied) => [0x0A]
	  | (ASL,expr as ZeroPage _) => operandBytes 0x06 expr
	  | (ASL,expr as ZeroPageX _) => operandBytes 0x16 expr
	  | (ASL,expr as Absolute _) => operandBytes 0x0E expr
	  | (BCC,Relative expr) => branch 0x90 expr
	  | (BCS,Relative expr) => branch 0xB0 expr
	  | (BEQ,Relative expr) => branch 0xF0 expr
	  | (BMI,Relative expr) => branch 0x30 expr
	  | (BNE,Relative expr) => branch 0xD0 expr
	  | (BPL,Relative expr) => branch 0x10 expr
	  | (BRK,Implied) => [0x00]
	  | (BVC,Relative expr) => branch 0x50 expr
	  | (BVS,Relative expr) => branch 0x70 expr
	  | (CLC,Implied) => [0x18]
	  | (CLD,Implied) => [0xD8]
	  | (CLI,Implied) => [0x58]
	  | (CLV,Implied) => [0xB8]
	  | (CMP,operand) => modes (0xC9,0xC5,0xD5,0xCD,0xDD,0xD9,0xC1,0xD1) operand
	  | (CPX,operand as Immediate _) => operandBytes 0xE0 operand
	  | (CPX,operand as ZeroPage _) => operandBytes 0xE4 operand
	  | (CPX,operand as Absolute _) => operandBytes 0xEC operand
	  | (CPY,operand as Immediate _) => operandBytes 0xC0 operand
	  | (CPY,operand as ZeroPage _) => operandBytes 0xC4 operand
	  | (CPY,operand as Absolute _) => operandBytes 0xCC operand
	  | (DEC,operand as ZeroPage _) => operandBytes 0xC6 operand
	  | (DEC,operand as ZeroPageX _) => operandBytes 0xD6 operand
	  | (DEC,operand as Absolute _) => operandBytes 0xCE operand
	  | (DEC,operand as AbsoluteX _) => operandBytes 0xDE operand
	  | (DEX,Implied) => [0xCA]
	  | (DEY,Implied) => [0x88]
	  | (EOR,operand) => modes (0x49,0x45,0x55,0x4D,0x5D,0x59,0x41,0x51) operand
	  | (INC,operand as ZeroPage _) => operandBytes 0xE6 operand
	  | (INC,operand as ZeroPageX _) => operandBytes 0xF6 operand
	  | (INC,operand as Absolute _) => operandBytes 0xEE operand
	  | (INC,operand as AbsoluteX _) => operandBytes 0xFE operand
	  | (INX,Implied) => [0xE8]
	  | (INY,Implied) => [0xC8]
	  | (JMP,operand as Absolute _) => operandBytes 0x4C operand
	  | (JMP,operand as Indirect _) => operandBytes 0x6C operand
	  | (JSR,operand as Absolute _) => operandBytes 0x20 operand
	  | (LDA,operand) => modes (0xA9,0xA5,0xB5,0xAD,0xBD,0xB9,0xA1,0xB1) operand
	  | (LDX,operand as Immediate _) => operandBytes 0xA2 operand
	  | (LDX,operand as ZeroPage _) => operandBytes 0xA6 operand
	  | (LDX,operand as ZeroPageY _) => operandBytes 0xB6 operand
	  | (LDX,operand as Absolute _) => operandBytes 0xAE operand
	  | (LDX,operand as AbsoluteY _) => operandBytes 0xBE operand
	  | (LDY,operand as Immediate _) => operandBytes 0xA0 operand
	  | (LDY,operand as ZeroPage _) => operandBytes 0xA4 operand
	  | (LDY,operand as ZeroPageY _) => operandBytes 0xB4 operand
	  | (LDY,operand as Absolute _) => operandBytes 0xAC operand
	  | (LDY,operand as AbsoluteY _) => operandBytes 0xBC operand
	  | (LSR,operand) => rotate (0x4A,0x46,0x56,0x4E,0x5E) operand
	  | (NOP,Implied) => [0xEA]
	  | (ORA,operand) => modes (0x09,0x05,0x15,0x0D,0x1D,0x19,0x01,0x11) operand
	  | (PHA,Implied) => [0x48]
	  | (PHP,Implied) => [0x08]
	  | (PLA,Implied) => [0x68]
	  | (PLP,Implied) => [0x28]
	  | (ROL,operand) => rotate (0x2A,0x26,0x36,0x2E,0x3E) operand
	  | (ROR,operand) => rotate (0x6A,0x66,0x76,0x6E,0x7E) operand
	  | (RTI,Implied) => [0x40]
	  | (RTS,Implied) => [0x60]
	  | (SBC,operand) => modes (0xE9,0xE5,0xF5,0xED,0xFD,0xF9,0xE1,0xF1) operand
	  | (SEC,Implied) => [0x38]
	  | (SED,Implied) => [0xF8]
	  | (STA,Immediate _) => raise AssemblyError BadAddress
	  | (STA,operand) => modes (0x00,0x85,0x95,0x8D,0x9D,0x99,0x81,0x91) operand
	  | (STX,operand as ZeroPage _) => operandBytes 0x86 operand
	  | (STX,operand as ZeroPageY _) => operandBytes 0x96 operand
	  | (STX,operand as Absolute _) => operandBytes 0x8E operand
	  | (STY,operand as ZeroPage _) => operandBytes 0x84 operand
	  | (STY,operand as ZeroPageY _) => operandBytes 0x94 operand
	  | (STY,operand as Absolute _) => operandBytes 0x8C operand
	  | (TAX,Implied) => [0xAA]
	  | (TAY,Implied) => [0xA8]
	  | (TSX,Implied) => [0xBA]
	  | (TXA,Implied) => [0x8A]
	  | (TXS,Implied) => [0x9A]
	  | (TYA,Implied) => [0x98]
	  | _ => raise AssemblyError BadAddress
    end

fun pass2 (sourceLine,inst,addr,map,listFn) =
    let
	val bytes = instBytes (inst,addr,map)
    in
	listFn (Printing.formatLine (SOME addr,bytes,File.data sourceLine))
    end
end
