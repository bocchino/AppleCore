LIB=${APPLECORE}/Lib
TF=HEXCONVERT.OBJ
DECLS=${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${TF}
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm -d obj ${OPTS}

all : obj all-avm
	${SC-AVM-ASM} avm/HEXCONVERT.avm

include ${APPLECORE}/Makefile.ac.common
