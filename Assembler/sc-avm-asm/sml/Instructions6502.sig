signature INSTRUCTIONS6502 =
  sig

  datatype operand =
    None
  | ImmediateLow of Parser.expr
  | ImmediateHigh of Parser.expr
  | Direct of Parser.expr
  | DirectX of Parser.expr
  | DirectY of Parser.expr
  | Indirect of Parser.expr
  | IndirectX of Parser.expr
  | IndirectY of Parser.expr

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

  val getMnemonic : string -> mnemonic option
  val parseOperand : Substring.substring -> operand option * Substring.substring
  val parseInstruction : Substring.substring -> instruction option * Substring.substring

  end
