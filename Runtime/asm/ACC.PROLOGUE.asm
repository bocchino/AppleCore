* -------------------------------
* THE APPLECORE COMPILER, V1.0
* PROGRAM PROLOGUE
* -------------------------------
* REFERENCE LABELS
* -------------------------------
ACC.SP	.EQ $00	STACK POINTER
ACC.FP	.EQ $02	FRAME POINTER
ACC.IP	.EQ $04	INDEX POINTER
ACC.SIZE  .EQ $06 SIZE REG
ACC.GR    .EQ $07 GENERAL REG
ACC.IDX.1 .EQ $08 INDEX REG 1
ACC.IDX.2 .EQ $09 INDEX REG 2
* -------------------------------
* INIT SP AND FP
* -------------------------------
ACC.START
	LDA #ACC.STACK
	STA ACC.SP
	STA ACC.FP
	LDA /ACC.STACK
	STA ACC.SP+1
	STA ACC.FP+1

