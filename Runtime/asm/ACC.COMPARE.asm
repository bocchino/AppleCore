* -------------------------------
* THE APPLECORE COMPILER, V1.0
* COMPARISON OPERATIONS
* -------------------------------
* SET A = SIZE
* SET SP-=2*SIZE
* COMPARE SP[0,SIZE]
* 	TO SP[SIZE,SIZE]
* COMPARISON IS SIGNED IF X=/=0
* PUSH 1=EQ, 0=NEQ
* -------------------------------
ACC.BINOP.EQ
        JSR ACC.CMP
	BEQ ACC.PUSH.1
	BNE ACC.PUSH.0
* -------------------------------
* ...PUSH 1=GT, 0=LEQ
* -------------------------------
ACC.BINOP.GT
        JSR ACC.CMP
        BEQ ACC.PUSH.0
	BNE ACC.BINOP.GEQ.1
* -------------------------------
* ...PUSH 1=GEQ, 0=LT
* -------------------------------
ACC.BINOP.GEQ
        JSR ACC.CMP
ACC.BINOP.GEQ.1
	BCC ACC.PUSH.0
	BCS ACC.PUSH.1
* -------------------------------
* ...PUSH 1=LEQ, 0=GT
* -------------------------------
ACC.BINOP.LEQ
        JSR ACC.CMP
	BEQ ACC.PUSH.1
	BNE ACC.BINOP.LT.1
* -------------------------------
* ...PUSH 1=LT, 0=GEQ
* -------------------------------
ACC.BINOP.LT
        JSR ACC.CMP
ACC.BINOP.LT.1
        BCC ACC.PUSH.1
* -------------------------------
* SET SIZE=1
* PUSH 0
* -------------------------------
ACC.PUSH.0
	LDA #0
	JMP ACC.PUSH.A
* -------------------------------
* SET SIZE=1
* PUSH 1
* -------------------------------
ACC.PUSH.1
	LDA #1
        JMP ACC.PUSH.A
* -------------------------------
* SET SIZE=A
* SET SP-=2*SIZE
* COMPARE SP[SIZE,A] TO
*	SP[SIZE,SIZE]
* COMPARISON IS SIGNED IF X=/=0
* SET C=GEQ,Z=EQ
* -------------------------------
ACC.CMP
* IP=SP-SIZE, SP-=2*SIZE
        JSR ACC.INIT.BINOP
* Y=SIZE
        LDY ACC.SIZE
* X=SIGNEDNESS
        TXA
        BEQ ACC.CMP.U
* SIGNED, CHECK SIGN BITS
        DEY
        LDA (ACC.SP),Y
	EOR (ACC.IP),Y
	AND #$80
	BEQ ACC.CMP.U.1
* SIGN BITS UNEQUAL
	SEC
	LDA (ACC.IP),Y
* NOT ZERO
	ORA #1
* SIGN BIT INTO CARRY
	ROL
	RTS
* -------------------------------
* COMPARE SP[0,Y] TO IP[0,Y]
* COMPARISON IS UNSIGNED
* SET C=GEQ,Z=EQ
* -------------------------------
ACC.CMP.U
	DEY
ACC.CMP.U.1
        LDA (ACC.SP),Y  
        CMP (ACC.IP),Y
        BEQ .1
* UNEQUAL, WE'RE DONE
	RTS
* EQUAL, KEEP GOING
.1	TYA
        BNE ACC.CMP.U
* CHECKED ALL BYTES
	RTS
* -------------------------------
* FN:1S CMP(A:2,B:2,S:1)
* COMPARE A[0,2][0,S] WITH
* 	B[0,2][0,S]
* RESULT IS -1=LT,0=EQ,1=GT
* -------------------------------
CMP
	LDA #7
	JSR ACC.EVAL.A.B.1
	LDY ACC.SIZE
	JSR ACC.CMU.U
	BEQ .1
	BCS .2
* NEGATIVE, RESULT=-1
	LDA #$FF
	BMI .3
* EQUAL, RESULT=0
.1	LDA #0
	BEQ .3
* POSITIVE, RESULT=1
.2	LDA #1
.3	PHA
* RESTORE FP AND SP
	JSR ACC.FN.RETURN
	PLA
* PUSH RESULT
	JMP ACC.PUSH.A
