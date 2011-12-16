* -------------------------------
* THE APPLECORE COMPILER, V1.0
* GENERAL FUNCTIONS
* -------------------------------
* SET FP=SP
* PRESERVES X,Y
* -------------------------------
ACC.SET.FP.TO.SP
        LDA ACC.SP
        STA ACC.FP
        LDA ACC.SP+1
        STA ACC.FP+1
        RTS
* -------------------------------
* SET SP=FP
* PRESERVES X,Y
* -------------------------------
ACC.SET.SP.TO.FP
        LDA ACC.FP
        STA ACC.SP
        LDA ACC.FP+1
        STA ACC.SP+1
	RTS
* -------------------------------
* SET IP=SP
* PRESERVES X,Y
* -------------------------------
ACC.SET.IP.TO.SP
        LDA ACC.SP
        STA ACC.IP
        LDA ACC.SP+1
        STA ACC.IP+1
        RTS
* -------------------------------
* SET SP=IP
* PRESERVES X,Y
* -------------------------------
ACC.SET.SP.TO.IP
        LDA ACC.IP
        STA ACC.SP
        LDA ACC.IP+1
        STA ACC.SP+1
        RTS
* -------------------------------
* SET FP=FP[0,2]
* SET Y=1
* PRESERVES X
* -------------------------------
ACC.RESTORE.CALLER.FP
        LDY #0
        LDA (ACC.FP),Y
        PHA
        INY
        LDA (ACC.FP),Y
        STA ACC.FP+1
        PLA
        STA ACC.FP
        RTS
* -------------------------------
* SET SP[0,2]=FP
* SET SP+=2
* SET Y=1
* PRESERVES X
* -------------------------------
ACC.PUSH.FP
        LDA #0
* -------------------------------
* SET SP[0,2]=FP+A
* SET SIZE=2
* SET SP+=2
* SET Y=1
* PRESERVES X
* -------------------------------
ACC.PUSH.SLOT
        LDY #0
        CLC
        ADC ACC.FP
        STA (ACC.SP),Y
        INY
        LDA ACC.FP+1
        ADC #0
        STA (ACC.SP),Y
        LDA #2
* -------------------------------
* SET SIZE=A
* SET SP+=SIZE
* PRESERVES X,Y
* -------------------------------
ACC.SP.UP.A
	STA ACC.SIZE
* -------------------------------
* SET SP+=SIZE
* PRESERVES X,Y
* -------------------------------
ACC.SP.UP.SIZE
	LDA ACC.SP
        CLC
        ADC ACC.SIZE
        STA ACC.SP
        BCC .1
        INC ACC.SP+1
.1      RTS
* -------------------------------
* SET SIZE=A
* SET SP-=SIZE
* PRESERVES X,Y
* -------------------------------
ACC.SP.DOWN.A
        STA ACC.SIZE
* -------------------------------
* SET SP-=SIZE
* PRESERVES X,Y
* -------------------------------
ACC.SP.DOWN.SIZE
        LDA ACC.SP
        SEC
        SBC ACC.SIZE
        STA ACC.SP
        BCS .1
        DEC ACC.SP+1
.1      RTS
* -------------------------------
* SET SP-=2
* SET IP=SP[0,2]
* CLOBBERS X,Y
* PRESERVES SIZE
* -------------------------------
ACC.POP.IP
	LDX ACC.SIZE
	JSR ACC.POP.A
	STA ACC.IP+1
	JSR ACC.POP.A
	STA ACC.IP
	STX ACC.SIZE
        RTS
* -------------------------------
* SET SP-=2
* SET IP=SP[0,2]
* SET SIZE=A
* SET SP[0,SIZE]=IP[0,SIZE]
* SET SP+=SIZE
* SET X=SIZE
* SET Y=0
* -------------------------------
ACC.EVAL
        STA ACC.SIZE
        JSR ACC.POP.IP
* -------------------------------
* SET SP[0,SIZE]=IP[0,SIZE]
* SET SP+=SIZE
* CLOBBERS Y
* -------------------------------
ACC.EVAL.1
	LDY ACC.SIZE
.1      DEY
        LDA (ACC.IP),Y
        STA (ACC.SP),Y
        TYA
        BNE .1
        JMP ACC.SP.UP.SIZE
* -------------------------------
* SET SP-=2
* SET IP=SP[0,2]
* SET SIZE=A
* SET SP-=SIZE
* SET IP[0,SIZE]=SP[0,SIZE]
* SET X=SIZE
* CLOBBERS Y
* -------------------------------
ACC.ASSIGN
        STA ACC.SIZE
        JSR ACC.POP.IP
ACC.ASSIGN.1
        JSR ACC.SP.DOWN.SIZE
	LDY ACC.SIZE
.1      DEY
        LDA (ACC.SP),Y
        STA (ACC.IP),Y
        TYA
        BNE .1
        RTS
* -------------------------------
* SET Y,SIZE=A
* PUSH SIZE 0'S OR $FF'S
* ON ENTRY X MUST BE 0 OR 1
* SIGN EXTEND IF X=MSb=1
* ZERO EXTEND OTHERWISE
* CLOBBERS X
* -------------------------------
ACC.EXTEND
        STA ACC.SIZE
	TXA
	BEQ ACC.PUSH.X.SIZE.TIMES
* X=1, LOOK AT MSb
	DEX
        JSR ACC.SP.DOWN.SIZE
	LDY ACC.SIZE
	DEY
	LDA (ACC.SP),Y
        AND #$80
        PHA
        JSR ACC.SP.UP.SIZE
	PLA
        BEQ ACC.PUSH.X.SIZE.TIMES
* SET X=$FF
	DEX
* -------------------------------
* SET SP[0,SIZE]=X, SP+=SIZE
* SET Y=A
* PRESERVES X
* -------------------------------
ACC.PUSH.X.SIZE.TIMES
	LDY ACC.SIZE
.1	DEY
	TXA
	STA (ACC.SP),Y
	TYA
	BNE .1
	JMP ACC.SP.UP.SIZE
* -------------------------------
* SET SP[0,2]=FP
* SET SP+=2
* SET Y=0
* SET SIZE=1
* PRESERVES X
* -------------------------------
ACC.PUSH.SP
	LDA ACC.SP
        JSR ACC.PUSH.A
        LDA ACC.SP+1
* -------------------------------
* SET SP[0,1]=A
* INC SP
* SET Y=0
* SET SIZE=1
* PRESERVES X
* -------------------------------
ACC.PUSH.A
        LDY #0
        STA (ACC.SP),Y
        LDA #1
        JMP ACC.SP.UP.A
* -------------------------------
* DEC SP
* SET SIZE=1
* SET A=SP[0,1]
* SET Y=0
* -------------------------------
ACC.POP.A
	LDA #1
	JSR ACC.SP.DOWN.A
	LDY #0
	LDA (ACC.SP),Y
	RTS
* -------------------------------
* SET SIZE=A
* SET SP-=SIZE
* SET A=(SP[0,1] AND $01)
* -------------------------------
ACC.BOOLEAN
        JSR ACC.SP.DOWN.A
        LDY #0
        LDA (ACC.SP),Y
        AND #1
        RTS
* -------------------------------
* SET SP-=2
* SET IP=SP[0,2]
* SET SIZE=2
* SET Y=1
* PRESERVES X
* -------------------------------
ACC.INDIRECT.CALL
	JSR ACC.POP.IP
	JMP (ACC.IP)
* -------------------------------
* INITIALIZATION FOR BINARY OPS
* SET SIZE=A
* SET IP=SP-SIZE
* SET SP-=2*SIZE
* PRESERVES X,Y
* -------------------------------
ACC.INIT.BINOP
        JSR ACC.SP.DOWN.A
        JSR ACC.SET.IP.TO.SP
        JSR ACC.SP.DOWN.SIZE
        RTS
* -------------------------------
* SET A=SP[SIZE-1,1]
* -------------------------------
ACC.GET.MSB
	LDY ACC.SIZE
	DEY
	LDA (ACC.SP),Y
	RTS
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
	LDY #2
	JSR ACC.SET.IP.TO.VAR
	JSR ACC.EVAL.1
* EVAL B AND RETURN
	LDY #4
	JSR ACC.SET.IP.TO.VAR
	JMP ACC.EVAL.1
* -------------------------------
* ASSIGN C AND RETURN
* -------------------------------
ACC.ASSIGN.C.AND.RET
	LDY #6
ACC.ASSIGN.AND.RET
	JSR ACC.SET.IP.TO.VAR
	JSR ACC.ASSIGN.1
* -------------------------------
* RESTORE OLD FRAME AND RETURN
* -------------------------------
ACC.FN.RETURN
	JSR ACC.SET.SP.TO.FP
	JMP ACC.RESTORE.CALLER.FP
* -------------------------------
* SET IP=ACC.FP[Y,2]
* -------------------------------
ACC.SET.IP.TO.VAR
	LDA (ACC.FP),Y
	STA ACC.IP
	INY
	LDA (ACC.FP),Y
	STA ACC.IP+1
	RTS
* -------------------------------
* FN:2 ALLOCATE(S:1)
* RETURN PTR TO S ZEROS ON STACK
* -------------------------------
ALLOCATE
* GET SIZE OFF STACK
	LDY #2
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
	