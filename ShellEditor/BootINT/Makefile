LIB=${APPLECORE}/Lib
DECLS=${LIB}/ach/MON.ach:${LIB}/ac/IO.ac:${LIB}/ac/DOS.ac:${LIB}/ac/BANK.SWITCHES.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=HELLO.OBJ -origin=8192
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm -d obj ${OPTS}

all : obj all-avm
	${SC-AVM-ASM} avm/HELLO.avm

include ${APPLECORE}/Makefile.ac.common


