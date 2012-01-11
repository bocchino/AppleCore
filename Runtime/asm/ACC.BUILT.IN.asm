* -------------------------------
* THE APPLECORE COMPILER, V1.0
* BUILT-IN FUNCTIONS
* -------------------------------
* FN:2 ALLOCATE(S:1)
* RETURN PTR TO S ZEROS ON STACK
* -------------------------------
ALLOCATE
	JSR ACC.FN.PROLOGUE
* GET SIZE OFF STACK
	LDY #4
	LDA (ACC.FP),Y
	STA ACC.SIZE
* SAVE SP INTO IP
	JSR ACC.SET.IP.TO.SP
* RESTORE OLD FRAME
	JSR ACC.FN.RETURN
* PUSH SIZE ZEROS ON STACK
	LDX #0
	JSR ACC.PUSH.X.SIZE.TIMES
* PUSH IP
	LDA ACC.IP
	JSR ACC.PUSH.A
	LDA ACC.IP+1
	JMP ACC.PUSH.A
* -------------------------------
* FN ADD(A:2,B:2,C:2,S:1)
* SET SIZE=FP[10,1]
* SET FP[8,2][0,SIZE]=
*   	FP[4,2][0,SIZE]+
*	FP[6,2][0,SIZE]
* -------------------------------
ADD
	JSR ACC.FN.PROLOGUE
	LDA #11
	JSR ACC.EVAL.A.AND.B
	LDA ACC.SIZE
	JSR ACC.BINOP.ADD
	JMP ACC.ASSIGN.C.AND.RET
* -------------------------------
* FN SUB(A:2,B:2,C:2,S:1)
* SET SIZE=FP[10,1]
* SET FP[8,2][0,SIZE]=
*   	FP[6,2][0,SIZE]-
*	FP[4,2][0,SIZE]
* -------------------------------
SUB
	JSR ACC.FN.PROLOGUE
	LDA #11
	JSR ACC.EVAL.A.AND.B
	LDA ACC.SIZE
	JSR ACC.BINOP.SUB
	JMP ACC.ASSIGN.C.AND.RET
* -------------------------------
* FN MUL(A:2,B:2,C:2,S:1)
* SET SIZE=FP[10,1]
* SET FP[8,2][0,SIZE]=
*   	FP[4,2][0,SIZE]*
*	FP[6,2][0,SIZE]
* -------------------------------
MUL
	JSR ACC.FN.PROLOGUE
	LDA #11
	JSR ACC.EVAL.A.AND.B
	LDA ACC.SIZE
	LDX #0
	JSR ACC.BINOP.MUL
	JMP ACC.ASSIGN.C.AND.RET
* -------------------------------
* FN DIV(A:2,B:2,Q:2,R:2,S:1)
* SET SIZE=FP[12,1]
* DIVIDE FP[4,2][0,SIZE]
* 	BY FP[6,2][0,SIZE]
* QUOTIENT IN FP[8,2][0,SIZE]
* REMAINDER IN FP[10,2][0,SIZE]
* -------------------------------
DIV
	JSR ACC.FN.PROLOGUE
	LDA #13
	JSR ACC.EVAL.A.AND.B
	LDA ACC.SIZE
	LDX #0
	JSR ACC.BINOP.DIV
* ASSIGN QUOT
	LDY #8
	JSR ACC.SET.IP.TO.VAR
	JSR ACC.ASSIGN.1
* ASSIGN REM AND RETURN
	JSR ACC.SP.UP.SIZE
	JSR ACC.SP.UP.SIZE
	JSR ACC.SP.UP.SIZE
	LDY #10
	JMP ACC.ASSIGN.AND.RET
* -------------------------------
* FN:1S CMP(A:2,B:2,S:1)
* COMPARE A[0,2][0,S] WITH
* 	B[0,2][0,S]
* RESULT IS -1=LT,0=EQ,1=GT
* -------------------------------
CMP
	JSR ACC.FN.PROLOGUE
* EVALUATE ARGS AND COMPARE
	LDA #9
	JSR ACC.EVAL.A.AND.B
	LDA ACC.SIZE
	LDX #0
	JSR ACC.CMP
* TEST RESULT
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
* -------------------------------
* BUILT-IN FUNCTION PROLOGUE
* -------------------------------
ACC.FN.PROLOGUE
	LDA #2
	JSR ACC.SP.UP.A
	JSR ACC.PUSH.FP
	LDA #4
	JSR ACC.SP.DOWN.A
	JMP ACC.SET.FP.TO.SP
* -------------------------------
* EVALUATE A AND B ON STACK
* -------------------------------
ACC.EVAL.A.AND.B
* BUMP STACK TO TOP OF FRAME
	TAY
	JSR ACC.SP.UP.A
* SET SIZE
	DEY
	LDA (ACC.FP),Y
	STA ACC.SIZE
* EVAL A
	LDY #4
	JSR ACC.SET.IP.TO.VAR
	JSR ACC.EVAL.1
* EVAL B AND RETURN
	LDY #6
	JSR ACC.SET.IP.TO.VAR
	JMP ACC.EVAL.1
* -------------------------------
* ASSIGN C AND RETURN
* -------------------------------
ACC.ASSIGN.C.AND.RET
	LDY #8
ACC.ASSIGN.AND.RET
	JSR ACC.SET.IP.TO.VAR
	JSR ACC.ASSIGN.1
	JMP ACC.FN.RETURN

