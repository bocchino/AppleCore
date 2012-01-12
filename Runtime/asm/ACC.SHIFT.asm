:NEW
* -------------------------------
* THE APPLECORE COMPILER, V1.0
* SHIFT OPERATIONS
* -------------------------------
* SET SIZE=A
* SET SP-=(SIZE+1)
* SET GR=SP[SIZE,1]
* SET SP[0,SIZE]<<=GR
* SET SP+=SIZE
* -------------------------------
ACC.BINOP.SHL
	JSR ACC.INIT.SHIFT
* OUTER LOOP OVER SHAMT
	LDX ACC.GR
* INNER LOOP OVER SIZE
.1	JSR ACC.SHL.INNER
	DEX
	BNE .1
	JMP ACC.SP.UP.SIZE
* -------------------------------
* SET SIZE=A
* SET SP-=(SIZE+1)
* SET GR=SP[SIZE,1]
* SET SP[0,SIZE]>>=GR
* SHIFT IS SIGNED IF X=/=0
* SET SP+=SIZE
* CLOBBERS IDX.2
* -------------------------------
ACC.BINOP.SHR
	JSR ACC.INIT.SHIFT
* STORE SHAMT IN IDX.1
	LDA ACC.GR
	STA ACC.IDX.1
* CHECK SIGNEDNESS
	TXA
	BEQ .1
* SIGNED SHIFT, SET X=MSb
	LDY ACC.SIZE
	DEY
	LDA (ACC.SP),Y
	TAX
* OUTER LOOP OVER SHAMT
.1	ASL
* INNER LOOP OVER SIZE
	JSR ACC.SHR.INNER
	TXA
	DEC ACC.IDX.1
	BNE .1
	JMP ACC.SP.UP.SIZE
* -------------------------------
* INITIALIZE SHIFT OP
* SET SIZE=A
* SET SP-=(SIZE+1)
* SET GR=SHAMT
* PRESERVES X
* -------------------------------
ACC.INIT.SHIFT
	PHA
* POP AND STORE SHAMT
	JSR ACC.POP.A
	STA ACC.GR
* ADJUST SP AND STORE SIZE
	PLA
	JMP ACC.SP.DOWN.A
* -------------------------------
* INNER LOOP FOR SHR
* -------------------------------
ACC.SHR.INNER
	LDY ACC.SIZE
.2	DEY
	LDA (ACC.SP),Y
	ROR
	STA (ACC.SP),Y
	TYA
	BNE .2
	RTS	
* -------------------------------
* INNER LOOP FOR SHL
* CLOBBERS IDX.1
* -------------------------------
ACC.SHL.INNER
	CLC
ACC.SHL.INNER.1
	LDY #0
	LDA ACC.SIZE
	STA ACC.IDX.1
.1	LDA (ACC.SP),Y
	ROL
	STA (ACC.SP),Y
	INY
	DEC ACC.IDX.1
	BNE .1
	RTS