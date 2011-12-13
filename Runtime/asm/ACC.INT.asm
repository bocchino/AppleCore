* -------------------------------
* THE APPLECORE COMPILER, V1.0
* INTEGER ARITHMETIC OPERATIONS
* -------------------------------
* SET SIZE=A
* SET SP-=2*SIZE
* SET SP[0,SIZE]+=SP[SIZE,SIZE]
* SET SP+=SIZE
* -------------------------------
ACC.BINOP.ADD
	JSR ACC.INIT.BINOP
	JSR ACC.ADD
        JMP ACC.SET.SP.TO.IP
* -------------------------------
* SET SP[0,SIZE]+=IP[0,SIZE]
* CLOBBERS X,Y
* -------------------------------
ACC.ADD
	LDX ACC.SIZE
	LDY #0
        CLC
.1      LDA (ACC.SP),Y
        ADC (ACC.IP),Y
        STA (ACC.SP),Y
        INY
        DEX
        BNE .1
	RTS
* -------------------------------
* SET A=SIZE
* SET SP-=2*SIZE
* SET SP[0,SIZE]-=SP[A,SIZE]
* SET SP+=SIZE
* -------------------------------
ACC.BINOP.SUB
        JSR ACC.INIT.BINOP
	JSR ACC.SUB
        JMP ACC.SET.SP.TO.IP
* -------------------------------
* SET SP[0,SIZE]-=IP[0,SIZE]
* CLOBBERS X,Y
* -------------------------------
ACC.SUB
	LDX ACC.SIZE
	LDY #0
        SEC
.1      LDA (ACC.SP),Y
        SBC (ACC.IP),Y
        STA (ACC.SP),Y
        INY
        DEX
        BNE .1
        RTS
* -------------------------------
* SET SIZE=A
* SET SP[-2*SIZE,SIZE]
*	*=SP[-SIZE,SIZE]
* MULT IS SIGNED IF X=/=0
* SET SP-=SIZE
* CLOBBERS IDX.1,IDX.2,IP
* -------------------------------
ACC.BINOP.MUL
	STA ACC.SIZE
	STA ACC.IDX.1
* SAVE SIGNEDNESS
	TXA
	PHA
* CLEAR RESULT
	LDX #0
	JSR ACC.PUSH.X.SIZE.TIMES
* SET SP AND IP
	JSR ACC.SP.DOWN.SIZE
	JSR ACC.SP.DOWN.SIZE
	JSR ACC.SP.DOWN.SIZE
	JSR ACC.SET.IP.TO.SP
	JSR ACC.SP.UP.SIZE
* CHECK SIGNEDNESS
	PLA
	BNE ACC.MUL.SIGNED
* -------------------------------
* UNSIGNED MULTIPLICATION
* -------------------------------
ACC.MUL.UNSIGNED
* DO 8 BITS PER BYTE
.1	LDA #8
	STA ACC.IDX.2
.2	LDA ACC.IDX.1
	PHA
	JSR ACC.MUL.INNER
	PLA
	STA ACC.IDX.1
	DEC ACC.IDX.2
	BNE .2
	DEC ACC.IDX.1
	BNE .1
* SET SP AND MOVE RESULT INTO PLACE
	JSR ACC.SP.UP.SIZE
	JSR ACC.SET.IP.TO.SP
	JSR ACC.SP.DOWN.SIZE
	JSR ACC.SP.DOWN.SIZE
	JMP ACC.EVAL.1
* -------------------------------
* SIGNED MULTIPLICATION
* -------------------------------
ACC.MUL.SIGNED
* CHECK SIGN BIT OF LHS
	JSR ACC.SP.DOWN.SIZE
	JSR ACC.GET.MSB
	STA ACC.GR
	BPL .1
* NEGATIVE, NEGATE
	SEC
	JSR ACC.NOT
* CHECK SIGN BIT OF LHS
.1	JSR ACC.SP.UP.SIZE
	JSR ACC.GET.MSB
	PHA
	BPL .2
* NEGATIVE, NEGATE
	SEC
	JSR ACC.NOT
* COMPARE SIGN BITS OF LHS, RHS
.2      PLA
	EOR ACC.GR
	AND #$80
* DO MULTIPLICATION
	PHA
	JSR ACC.MUL.UNSIGNED
	PLA
	BNE .3
	RTS
* SIGN BITS UNEQ, NEGATE RESULT
.3	LDA ACC.SIZE
	JMP ACC.UNOP.NEG
* -------------------------------
* INNER LOOP FOR UNSIGNED MUL
* ON ENTRY IP POINTS TO LHS,
* SP POINTS TO RHS
* -------------------------------
ACC.MUL.INNER
* SHIFT RHS 1 TO RIGHT
	CLC
	JSR ACC.SHR.INNER
* CHECK LSB OF RHS
	BCC .1
* IF IT'S 1, ADD LHS TO RESULT
	JSR ACC.SP.UP.SIZE
	JSR ACC.ADD
	JSR ACC.SP.DOWN.SIZE
* SHIFT LHS 1 TO LEFT
.1	JSR ACC.SP.DOWN.SIZE
	JSR ACC.SHL.INNER
	JMP ACC.SP.UP.SIZE
* -------------------------------
* SET SIZE=A
* DIVIDE SP[-2*SIZE,SIZE]
* 	BY SP[-SIZE,SIZE]
* QUOTIENT IN SP[-2*SIZE,SIZE]
* REMAINDER IN SP[-SIZE,SIZE]
* DIV IS SIGNED IF X=/=0
* CLOBBERS IDX.1,IDX.2,IP,GR
* -------------------------------
ACC.BINOP.DIV
	STA ACC.SIZE
	STA ACC.IDX.1
* SAVE SIGNEDNESS
	TXA
	PHA
* CLEAR REMAINDER
	LDX #0
	JSR ACC.PUSH.X.SIZE.TIMES
* SET SP AND IP
	JSR ACC.SP.DOWN.SIZE
	JSR ACC.SP.DOWN.SIZE
	JSR ACC.SET.IP.TO.SP
	JSR ACC.SP.DOWN.SIZE
* CHECK SIGNEDNESS
	PLA
	BNE ACC.DIV.SIGNED
* -------------------------------
* UNSIGNED DIVISION
* -------------------------------
ACC.DIV.UNSIGNED
* DO 8 BITS PER BYTE
.1	LDA #8
	STA ACC.IDX.2
.2	LDA ACC.IDX.1
	PHA
	JSR ACC.DIV.INNER
	PLA
	STA ACC.IDX.1
	DEC ACC.IDX.2
	BNE .2
	DEC ACC.IDX.1
	BNE .1
* MOVE SP
	JMP ACC.SP.UP.SIZE
* -------------------------------
* SIGNED DIVISION
* -------------------------------
ACC.DIV.SIGNED
	JSR ACC.GET.MSB
	AND #$80
	STA ACC.GR
	BEQ .1
* LHS < 0, NEGATE
	SEC
	JSR ACC.NOT
.1	JSR ACC.SP.UP.SIZE
	JSR ACC.GET.MSB
	AND #$80
	PHA
	BEQ .2
* RHS < 0, NEGATE
	SEC
	JSR ACC.NOT
* DO THE DIVISION
.2	JSR ACC.SP.DOWN.SIZE
	JSR ACC.DIV.UNSIGNED
* COMPARE SIGNS OF LHS,RHS
	PLA
	EOR ACC.GR
	BNE .3
	RTS
* NEGATE QUOTIENT
.3	LDA ACC.SIZE
	JMP ACC.UNOP.NEG
* -------------------------------
* INNER LOOP FOR UNSIGNED DIV
* ON ENTRY SP POINTS TO
* LHS/QUOTIENT, IP POINTS TO RHS
* -------------------------------
ACC.DIV.INNER
* SHIFT LHS/QUOTIENT 1 TO LEFT
	JSR ACC.SHL.INNER
	PHP
	JSR ACC.SP.UP.SIZE
	JSR ACC.SP.UP.SIZE
* SHIFT LHS INTO REMAINDER
	PLP
	JSR ACC.SHL.INNER.1
* COMPARE REMAINDER TO RHS
	LDY ACC.SIZE
	JSR ACC.CMP.U
	BCS .1
* REMAINDER TOO SMALL
	JSR ACC.SP.DOWN.SIZE
	JMP ACC.SP.DOWN.SIZE
* SUBTRACT RHS FROM REMAINDER
.1	JSR ACC.SUB
* ADD 1 TO QUOTIENT
	JSR ACC.SP.DOWN.SIZE
	JSR ACC.SP.DOWN.SIZE
	LDY #0
	LDA (ACC.SP),Y
	ORA #1
	STA (ACC.SP),Y
	RTS

