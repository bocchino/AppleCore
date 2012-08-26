signature NATIVE =
sig
    
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
			    
    val parse : Substring.substring -> instruction option
						  
end
