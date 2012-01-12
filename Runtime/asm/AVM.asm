:NEW
* -------------------------------
* THE APPLECORE COMPILER, V1.0
* APPLECORE VIRTUAL MACHINE
* -------------------------------
* PROGRAM COUNTER
* -------------------------------
AVM.PCL	.EQ $3A
AVM.PCH .EQ $3B
* -------------------------------
* EXECUTE AN AVM FUNCTION
* -------------------------------
AVM.EXECUTE.FN
* SAVE ACC RETURN ADDRESS
	LDA AVM.PCL
	JSR ACC.PUSH.A
	LDA AVM.PCH
	JSR ACC.PUSH.A
* STORE PROGRAM COUNTER
	PLA
	STA AVM.PCL
	PLA
	STA AVM.PCH
* SAVE 6502 RETURN ADDRESS
	PLA
	JSR ACC.PUSH.A
	PLA
	JSR ACC.PUSH.A
* SAVE FP
	JSR ACC.PUSH.FP
* SET NEW FP
	JSR ACC.SET.FP.TO.SP
* INSTRUCTION LOOP
.1	JSR AVM.DO.INSTR
	JMP .1
* -------------------------------
* DO AN AVM INSTRUCTION
* -------------------------------
AVM.DO.INSTR
	JSR AVM.FETCH.BYTE
	STA ACC.IDX.1
	STA ACC.SIZE
	AND #$F8
	BEQ AVM.BRANCH
* -------------------------------
* SIZED OR SIGNED INSTRUCTION
* -------------------------------
	LSR
	LSR
	LSR
	CLC
	ADC #$07
	STA ACC.IDX.1
	CMP #$1C
	LDA ACC.SIZE
	BCS AVM.SIGNED
* -------------------------------
* SIZED
* -------------------------------
* GET SIZE
	AND #7
	BEQ AVM.FETCH.SIZE
	BNE AVM.STORE.SIZE
* -------------------------------
* SIGNED
* -------------------------------
AVM.SIGNED
* GET SIGN BIT
	AND #4
	LSR
	LSR
	TAX
* GET SIZE
	LDA ACC.SIZE
	AND #3
	BNE AVM.STORE.SIZE
AVM.FETCH.SIZE
 	JSR AVM.FETCH.BYTE
AVM.STORE.SIZE
	STA ACC.SIZE
* -------------------------------
* BRANCH TO HANDLER
* -------------------------------
AVM.BRANCH
	ASL ACC.IDX.1
	LDY ACC.IDX.1
	LDA AVM.INSTR.TBL+1,Y
	PHA
	LDA AVM.INSTR.TBL,Y
	PHA
	LDA ACC.SIZE
	RTS
* -------------------------------
* INSTRUCTION HANDLERS
* -------------------------------
AVM.BRK
	BRK
* -------------------------------
AVM.BRF	
	JSR ACC.POP.A
	AND #1
	BEQ AVM.BRU
* -------------------------------
* GET L,H INTO X,A
* -------------------------------
AVM.GET.L.H
	JSR AVM.FETCH.BYTE
	TAX
* -------------------------------
* INC PC AND FETCH INTO A
* -------------------------------
AVM.FETCH.BYTE
	JSR AVM.INC.PC
	LDY #0
	LDA (AVM.PCL),Y
	RTS
* -------------------------------
AVM.BRU
	JSR AVM.GET.L.H
	STA AVM.PCH
	STX AVM.PCL
	JMP AVM.DEC.PC
* -------------------------------
AVM.CFD
	JSR AVM.GET.L.H
	STA ACC.IP+1
	TXA
AVM.CFD.1
	STA ACC.IP
	JSR MON.RESTORE
	JSR ACC.INDIRECT.CALL
	JMP MON.SAVE
* -------------------------------
AVM.CFI
	JSR ACC.POP.A
	STA ACC.IP+1
	JSR ACC.POP.A
	JMP AVM.CFD.1
* -------------------------------
AVM.MTV
 	PHA
	JSR AVM.FETCH.BYTE
	TAX
	PLA
	TAY
	LDA $00,X
	STA (ACC.FP),Y
	RTS
* -------------------------------
AVM.PHC
	STA ACC.IDX.1
	BEQ .2
.1	JSR AVM.FETCH.BYTE
	JSR ACC.PUSH.A
	DEC ACC.IDX.1
	BNE .1
.2	RTS
* -------------------------------
AVM.RAF
* SAVE SIZE
	TAX
* SAVE PTR TO RETURN VALUE
	JSR ACC.SP.DOWN.A
	JSR ACC.SET.IP.TO.SP
* RESTORE SP
	JSR ACC.SET.SP.TO.FP
* RESTORE FP
	JSR ACC.POP.A
	STA ACC.FP+1
	JSR ACC.POP.A
	STA ACC.FP
* RESTORE 6502 RETURN ADDRESS
	JSR ACC.POP.A
	PHA
	JSR ACC.POP.A
	PHA
* RESTORE PROGRAM COUNTER
	JSR ACC.POP.A
	STA AVM.PCH
	JSR ACC.POP.A
	STA AVM.PCL
* EVALUATE RETURN VALUE
	STX ACC.SIZE
	BEQ .1
	JMP ACC.EVAL.1
.1	RTS
* -------------------------------
AVM.VTM
 	PHA
	JSR AVM.FETCH.BYTE
	TAX
	PLA
	TAY
	LDA (ACC.FP),Y
	STA $00,X
	RTS
* -------------------------------
* INCREMENT AVM PC
* -------------------------------
AVM.INC.PC
	INC AVM.PCL
	BNE .1
	INC AVM.PCH
.1	RTS
* -------------------------------
* DECREMENT AVM PC
* -------------------------------
AVM.DEC.PC
	LDA AVM.PCL
	BNE .1
	DEC AVM.PCH
.1	DEC AVM.PCL
	RTS
* -------------------------------
* INSTRUCTION HANDLER ADDRESSES
* -------------------------------
AVM.INSTR.TBL
	.DA AVM.BRK-1	     BRK
	.DA AVM.BRF-1	     BRF
	.DA AVM.BRU-1	     BRU
	.DA AVM.CFD-1	     CFD
	.DA AVM.CFI-1	     CFI
	.DA AVM.BRK-1	     ???
	.DA AVM.BRK-1	     ???
	.DA AVM.BRK-1	     ???
	.DA ACC.BINOP.ADD-1  ADD
	.DA ACC.BINOP.AND-1  ANL
	.DA ACC.UNOP.DECR-1  DCR
	.DA ACC.SP.DOWN.A-1  DSP
	.DA ACC.UNOP.INCR-1  ICR
	.DA ACC.SP.UP.A-1    ISP
	.DA ACC.EVAL-1	     MTS
	.DA AVM.MTV-1	     MTV
	.DA ACC.UNOP.NEG-1   NEG
	.DA ACC.UNOP.NOT-1   NOT
	.DA ACC.BINOP.OR-1   ORL
	.DA ACC.BINOP.XOR-1  ORX
	.DA AVM.PHC-1        PHC
	.DA ACC.PUSH.SLOT-1  PVA
	.DA AVM.RAF-1	     RAF
	.DA ACC.BINOP.SHL-1  SHL
	.DA ACC.ASSIGN-1     STM
	.DA ACC.BINOP.SUB-1  SUB
	.DA ACC.BINOP.EQ-1   TEQ
	.DA AVM.VTM-1        VTM
	.DA ACC.BINOP.DIV-1  DIV
	.DA ACC.EXTEND-1     EXT
	.DA ACC.BINOP.MUL-1  MUL
	.DA ACC.BINOP.SHR-1  SHR
	.DA ACC.BINOP.GEQ-1  TGE
	.DA ACC.BINOP.GT-1   TGT
	.DA ACC.BINOP.LEQ-1  TLE
	.DA ACC.BINOP.LT-1   TLT
	.DA AVM.BRK-1	     ???
	.DA AVM.BRK-1	     ???
	.DA AVM.BRK-1	     ???
