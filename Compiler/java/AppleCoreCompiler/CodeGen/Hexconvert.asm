PREAMBLE
* Save callee FP
	LDY #0
	LDA FP
	STA (SP),Y
	INY
	LDA FP+1
	STA (SP),Y
* Set FP to SP
	LDA SP
	STA FP
	LDA SP+1
	STA FP+1
* Fall through to stack bump

BUMP_STACK
* Bump stack pointer by X-reg
	CLC
	TXA
	ADC SP
	STA SP
	BCC .1
	INC SP+1
.1	RTS

EPILOGUE
* Restore SP to FP
	LDA FP
	CLC
	ADC #2
	STA SP
	BCC .1
	INC FP+1
* Restore FP to caller FP
.1	LDY #0
	LDA (FP),Y
	TAX
	INY
	LDA (FP),Y
	STA FP+1
	TXA
	STA FP
	RTS

ADD_INDEX_TO_IRA
* Add A-reg to IRA
	CLC
	ADC (IRA),Y
	STA IRA
	BCC .1
	INC IRA+1
.1	RTS

ADD_INDEX_TO_FP
* Add A-reg to FP
	CLC
	ADC (FP),Y
	STA IRA
	BCC .1
	INC IRA+1
.1	RTS

SP_TO_IRA
* Move SP to IRA
	LDA SP
	STA IRA
	LDA SP+1
	STA IRA+1
	LDA #0
	STY OFFSETA
