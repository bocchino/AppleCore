* -------------------------------
* THE APPLECORE COMPILER, V1.0
* BITWISE OPERATIONS
* -------------------------------
* SET SIZE=A
* SET SP-=2*SIZE
* SET SP[0,SIZE]=(SP[0,SIZE]
*	AND SP[SIZE,SIZE])
* -------------------------------
ACC.BINOP.AND
        JSR ACC.INIT.BINOP
	LDY #0
.1      LDA (ACC.SP),Y
        AND (ACC.IP),Y
        STA (ACC.SP),Y
        INY
	CPY ACC.SIZE
        BNE .1
        JMP ACC.SET.SP.TO.IP
* -------------------------------
* SET SIZE=A
* SET SP-=2*SIZE
* SET SP[0,SIZE]=(SP[0,SIZE]
*	OR SP[SIZE,SIZE])
* -------------------------------
ACC.BINOP.OR
	JSR ACC.INIT.BINOP
	LDY #0
.1      LDA (ACC.SP),Y
        ORA (ACC.IP),Y
        STA (ACC.SP),Y
        INY
	CPY ACC.SIZE
        BNE .1
        JMP ACC.SET.SP.TO.IP
* -------------------------------
* SET SIZE=A
* SET SP-=2*SIZE
* SET SP[0,SIZE]=(SP[0,SIZE]
*	XOR SP[SIZE,SIZE])
* -------------------------------
ACC.BINOP.XOR
	JSR ACC.INIT.BINOP
	LDY #0
.1      LDA (ACC.SP),Y
        EOR (ACC.IP),Y
        STA (ACC.SP),Y
        INY
        CPY ACC.SIZE
        BNE .1
        JMP ACC.SET.SP.TO.IP
