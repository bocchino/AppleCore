* -------------------------------
* THE APPLECORE COMPILER, V1.0
* APPLECORE VIRTUAL MACHINE
* -------------------------------
* PROGRAM COUNTER
AVM.PCL	.EQ $3A
AVM.PCH .EQ $3B
* -------------------------------
* EXECUTE AN AVM FUNCTION
* -------------------------------
AVM.EXECUTE.FN
	JSR MON.SAVE
* SAVE ACC RETURN ADDRESS
	LDA MON.PCL
	JSR ACC.PUSH.A
	LDA MON.PCH
	JSR ACC.PUSH.A
* STORE PROGRAM COUNTER
	PLA
	STA MON.PCL
	PLA
	STA MON.PCH
* SAVE 6502 RETURN ADDRESS
	PLA
	JSR ACC.PUSH.A
	PLA
	JSR ACC.PUSH.A
* SAVE FP
	JSR ACC.PUSH.FP
* SET NEW FP
	LDA #6
	JSR ACC.SP.DOWN.A
	JSR.SET.FP.TO.SP
	JSR AVM.INC.PC
* INSTRUCTION LOOP
.1	JSR AVM.EXECUTE.INSR
	JMP .1
* -------------------------------
* DO ONE AVM INSTRUCTION
* -------------------------------
AVM.EXECUTE.INSTR
* FETCH NEXT INSTRUCTION
	LDY #0
	LDA (AVM.PCL),Y
	TAX
	AND #$F8
	BNE .1
* UNSIZED INSTRUCTION
	LDA AVM.UNSIZED.TBL,X
	JMP .2
* SIZED INSTRUCTION
.1	LSR
	LSR
	LSR
	TAY
	LDA AVM.SIZED.TBL,Y
* GET HANDLER ADDR ON STACK
.2	CLC
	ADC #AVM.BRK
	TAY
	LDA /AVM.BRK
	ADC #0
	PHA
	TYA
	PHA
* BRANCH TO HANDLER
	RTS
* -------------------------------
* OFFSETS TO UNSIZED INSTRS
* -------------------------------
AVM.UNSIZED.TBL
	.HS 00
	.DA #AVM.BRF-AVM.BRK
	.DA #AVM.BRU-AVM.BRK
	.DA #AVM.CFD-AVM.BRK
	.DA #AVM.CFI-AVM.BRK
	.HS 00
	.HS 00
	.HS 00
	.HS 00
* -------------------------------
* AVM BRK INSTRUCTION
* -------------------------------
AVM.BRK
	BRK
* -------------------------------
* AVM BRF INSTRUCTION
* -------------------------------
AVM.BRF	
	JSR ACC.POP.A
	AND #1
	BNE AVM.INC.PC.BY.2
* -------------------------------
* AVM BRU INSTRUCTION
* -------------------------------
AVM.BRU
	JSR AVM.GET.L.H
	STA AVM.PCH
	STX AVM.PCL
	RTS
* -------------------------------
* AVM CFD INSTRUCTION
* -------------------------------
AVM.CFD
	JSR AVM.GET.L.H
	PHA
	TXA
	PHA
	JMP AVM.INC.PC.BY.2
* -------------------------------
* AVM CFI INSTRUCTION
* -------------------------------
AVM.CFI
	JSR ACC.POP.A
	PHA
	JSR ACC.POP.A
	PHA
* -------------------------------
* INCREMENT AVM PC BY 2
* -------------------------------
AVM.INC.PC.BY.2
	JSR AVM.INC.PC
* -------------------------------
* INCREMENT AVM PC
* -------------------------------
AVM.INC.PC
	INC AVM.PCL
	BNE .1
	INC AVM.PCH
.1	RTS
* -------------------------------
* GET L,H INTO X,A
* -------------------------------
AVM.GET.L.H
	LDY #1
	LDA (AVM.PCL),Y
	TAX
	INY
	LDA (AVM.PCH),Y
	RTS
* -------------------------------
* OFFSETS TO SIZED INSTRS
* -------------------------------
AVM.SIZED.TBL
	.HS 00
	.HS #AVM.ANL-AVM.ADD
	.HS #AVM.DCR-AVM.ADD
	.HS #AVM.DSP-AVM.ADD
	.HS #AVM.ICR-AVM.ADD
	.HS #AVM.ISP-AVM.ADD
	.HS #AVM.MTS-AVM.ADD
	.HS #AVM.MTV-AVM.ADD
	.HS #AVM.NEG-AVM.ADD
	.HS #AVM.NOT-AVM.ADD
	.HS #AVM.ORL-AVM.ADD
	.HS #AVM.ORX-AVM.ADD
	.HS #AVM.PHC-AVM.ADD
	.HS #AVM.PVA-AVM.ADD
	.HS #AVM.RAF-AVM.ADD
	.HS #AVM.SHL-AVM.ADD
	.HS #AVM.STM-AVM.ADD
	.HS #AVM.SUB-AVM.ADD
	.HS #AVM.TEQ-AVM.ADD
	.HS #AVM.VTM-AVM.ADD
	.HS #AVM.DIV-AVM.ADD
	.HS #AVM.EXT-AVM.ADD
	.HS #AVM.MUL-AVM.ADD
	.HS #AVM.SHR-AVM.ADD
	.HS #AVM.TGE-AVM.ADD
	.HS #AVM.TGT-AVM.ADD
	.HS #AVM.TLE-AVM.ADD
	.HS #AVM.TLT-AVM.ADD
	.HS #AVM.UNUSED-AVM.ADD
	.HS #AVM.UNUSED-AVM.ADD
	.HS #AVM.UNUSED-AVM.ADD
	.HS #AVM.UNUSED-AVM.ADD
* -------------------------------
* AVM ADD INSTRUCTION
* -------------------------------
AVM.ADD
	RTS
* -------------------------------
* AVM ANL INSTRUCTION
* -------------------------------
AVM.ANL
	RTS
* -------------------------------
* AVM DCR INSTRUCTION
* -------------------------------
AVM.DCR
	RTS
* -------------------------------
* AVM DSP INSTRUCTION
* -------------------------------
AVM.DSP
	RTS
* -------------------------------
* AVM ICR INSTRUCTION
* -------------------------------
AVM.ICR
	RTS
* -------------------------------
* AVM ISP INSTRUCTION
* -------------------------------
AVM.ISP
	RTS
* -------------------------------
* AVM MTS INSTRUCTION
* -------------------------------
AVM.MTS
	RTS
* -------------------------------
* AVM MTV INSTRUCTION
* -------------------------------
AVM.MTV
	RTS
* -------------------------------
* AVM NEG INSTRUCTION
* -------------------------------
AVM.NEG
	RTS
* -------------------------------
* AVM NOT INSTRUCTION
* -------------------------------
AVM.NOT
	RTS
* -------------------------------
* AVM ORL INSTRUCTION
* -------------------------------
AVM.ORL
	RTS
* -------------------------------
* AVM ORX INSTRUCTION
* -------------------------------
AVM.ORX
	RTS
* -------------------------------
* AVM PHC INSTRUCTION
* -------------------------------
AVM.PHC
	RTS
* -------------------------------
* AVM PVA INSTRUCTION
* -------------------------------
AVM.PVA
	RTS
* -------------------------------
* AVM RAF INSTRUCTION
* -------------------------------
AVM.RAF
	RTS
* -------------------------------
* AVM SHL INSTRUCTION
* -------------------------------
AVM.SHL
	RTS
* -------------------------------
* AVM STM INSTRUCTION
* -------------------------------
AVM.STM
	RTS
* -------------------------------
* AVM SUB INSTRUCTION
* -------------------------------
AVM.SUB
	RTS
* -------------------------------
* AVM TEQ INSTRUCTION
* -------------------------------
AVM.TEQ
	RTS
* -------------------------------
* AVM VTM INSTRUCTION
* -------------------------------
AVM.VTM
	RTS
* -------------------------------
* AVM DIV INSTRUCTION
* -------------------------------
AVM.DIV
	RTS
* -------------------------------
* AVM EXT INSTRUCTION
* -------------------------------
AVM.EXT
	RTS
* -------------------------------
* AVM MUL INSTRUCTION
* -------------------------------
AVM.MUL
	RTS
* -------------------------------
* AVM SHR INSTRUCTION
* -------------------------------
AVM.SHR
	RTS
* -------------------------------
* AVM TGE INSTRUCTION
* -------------------------------
AVM.TGE
	RTS
* -------------------------------
* AVM TGT INSTRUCTION
* -------------------------------
AVM.TGT
	RTS
* -------------------------------
* AVM TLE INSTRUCTION
* -------------------------------
AVM.TLE
	RTS
* -------------------------------
* AVM TLT INSTRUCTION
* -------------------------------
AVM.TLT
	RTS
* -------------------------------
* AVM UNUSED INSTRUCTION
* -------------------------------
AVM.UNUSED
	BRK
