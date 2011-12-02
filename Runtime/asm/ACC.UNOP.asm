* -------------------------------
* THE APPLECORE COMPILER, V1.0
* UNARY OPERATIONS
* -------------------------------
* ARITHMETIC NEGATION
* SET SIZE=A
* SET SP[-SIZE,SIZE]*=-1
* -------------------------------
ACC.UNOP.NEG
	JSR ACC.SP.DOWN.A
	SEC
	BCS ACC.UNOP.NOT.1
* -------------------------------
* BITWISE NEGATION
* SET SIZE=A
* SET SP[-SIZE,SIZE]=
*	NOT [-SIZE,SIZE]
* -------------------------------
ACC.UNOP.NOT
	JSR ACC.SP.DOWN.A
	CLC
ACC.UNOP.NOT.1
	JSR ACC.NOT
	JMP ACC.SP.UP.SIZE
* -------------------------------
* FLIP THE BITS
* -------------------------------
ACC.NOT
	LDX ACC.SIZE
	LDY #0
.1	LDA (ACC.SP),Y
	EOR #$FF
	ADC #0
	STA (ACC.SP),Y
	INY
	DEX
	BNE .1
	RTS
* -------------------------------
* POP IP
* SET SIZE=A
* SET IP[0,SIZE]+=1
* SET SP[0,SIZE]=IP[0,SIZE]
* SET SP+=SIZE
* -------------------------------
ACC.UNOP.INCR
	STA ACC.SIZE
	TAX
	JSR ACC.POP.IP
	LDY #0
	SEC
.1	LDA (ACC.IP),Y
	ADC #0
	STA (ACC.IP),Y
	INY
	DEX
	BNE .1
	JMP ACC.EVAL.1
* -------------------------------
* POP IP
* SET SIZE=A
* SET IP[0,SIZE]-=1
* SET SP[0,SIZE]=IP[0,SIZE]
* SET SP+=SIZE
* -------------------------------
ACC.UNOP.DECR
	STA ACC.SIZE
	TAX
	JSR ACC.POP.IP
	LDY #0
	CLC
.1	LDA (ACC.IP),Y
	ADC #$FF
	STA (ACC.IP),Y
	INY
	DEX
	BNE .1
	JMP ACC.EVAL.1

        