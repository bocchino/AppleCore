* -------------------------------
* THE APPLECORE LIBRARY V1.0
* VARIABLE-SIZE INT ARITHMETIC
* -------------------------------
* SET SIZE=FP[8,1]
* SET FP[6,2][0,SIZE]=
*   	FP[2,2][0,SIZE]+
*	FP[4,2][0,SIZE]
* -------------------------------
ADD
	JSR EVAL.A.B
	LDA ACC.SIZE
	JSR ACC.BINOP.ADD
	JMP ASSIGN.C.AND.EXIT
* -------------------------------
* SET SIZE=FP[6,1]
* SET FP[4,2][0,SIZE]=
*   	FP[0,2][0,SIZE]-
*	FP[2,2][SP,0,SIZE]
* -------------------------------
SUB
	JSR EVAL.A.B
	LDA ACC.SIZE
	JSR ACC.BINOP.SUB
	JMP ASSIGN.C.AND.EXIT
* -------------------------------
* SET SIZE=FP[6,1]
* SET FP[4,2][0,SIZE]=
*   	FP[0,2][0,SIZE]*
*	FP[2,2][SP,0,SIZE]
* -------------------------------
MUL
	JSR EVAL.A.B
	LDA ACC.SIZE
	JSR ACC.BINOP.MUL
	JMP ASSIGN.C.AND.EXIT
* -------------------------------
* EVALUATE A AND B ON STACK
* -------------------------------
EVAL.A.B
* BUMP STACK TO TOP OF FRAME
	LDA #9
	JSR ACC.SP.UP.A
* SET SIZE
	LDY #8
	LDA (ACC.FP),Y
	STA ACC.SIZE
* EVAL A
	LDY #2
	JSR SET.IP.TO.VAR
	JSR ACC.EVAL.1
* EVAL B AND RETURN
	LDY #4
	JSR SET.IP.TO.VAR
	JMP ACC.EVAL.1
* -------------------------------
* ASSIGN C AND EXIT
* -------------------------------
ASSIGN.C.AND.EXIT
	LDY #6
	JSR SET.IP.TO.VAR
	JSR ACC.ASSIGN
* -------------------------------
* RESTORE OLD FRAME AND RETURN
* -------------------------------
EXIT	
	JSR ACC.SET.SP.TO.FP
	JMP ACC.RESTORE.CALLER.FP
* -------------------------------
* SET IP = ACC.FP[Y,2]
* -------------------------------
SET.IP.TO.VAR
	LDA (ACC.FP),Y
	STA ACC.IP
	INY
	LDA (ACC.FP),Y
	STA ACC.IP+1
	RTS



	
	
	
