LIB=../../Lib
DECLS=${LIB}/ach/MON.ach:${LIB}/ac/IO.ac:${LIB}/ac/PRINT.WORDS.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=HELLO.OBJ
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm:avm -d obj ${OPTS}

all : obj all-avm
	${SC-AVM-ASM} avm/HELLO.avm

include ${APPLECORE}/Makefile.ac.common.2

