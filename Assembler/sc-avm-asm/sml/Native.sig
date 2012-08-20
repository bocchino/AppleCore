signature NATIVE =
sig
    
    datatype operand =
	     None
	   | ImmediateLow of Operands.expr
	   | ImmediateHigh of Operands.expr
	   | Direct of Operands.expr
	   | DirectX of Operands.expr
	   | DirectY of Operands.expr
	   | Indirect of Operands.expr
	   | IndirectX of Operands.expr
	   | IndirectY of Operands.expr
			  
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
