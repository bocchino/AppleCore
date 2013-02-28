LIB=${APPLECORE}/Lib
DECLS=${LIB}/ac/IO.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS}

SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm -d obj ${OPTS}

all : obj all-avm
	${MAKE_ALL} avm obj

obj/${FILE}.obj : avm/${FILE}.avm obj
	${SC-AVM-ASM} avm/${FILE}.avm

include ${APPLECORE}/Makefile.ac.common
