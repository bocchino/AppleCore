LIB=../../Lib
ACC = ${APPLECORE}/Compiler/bin/acc -tf=TONE.OBJ
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm -d obj ${OPTS}

all : obj all-avm
	${SC-AVM-ASM} avm/TONE.avm

include ../../Makefile.ac.common.2

