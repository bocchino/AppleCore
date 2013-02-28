LIB=${APPLECORE}/Lib
LIB-AVM=${APPLECORE}/Lib-AVM
RUNTIME=${APPLECORE}/Runtime
DECLS=${LIB}/ac/IO.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=HELLO.WORLD.OBJ
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${LIB}/avm:${LIB-AVM}/avm:${RUNTIME}/avm -d obj ${OPTS}

all : obj all-avm
	${SC-AVM-ASM} avm/HELLO.WORLD.avm

include ${APPLECORE}/Makefile.ac.common
