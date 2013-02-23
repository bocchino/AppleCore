TARGET=SNAKE
LIB=${APPLECORE}/Lib
TF=$(TARGET).OBJ
DECLS=${LIB}/ach/MON.ach:${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ach/BOOLEAN.ach:${LIB}/ac/GRAPHICS.ac:${LIB}/ac/TONES.ac:${LIB}/ach/TONES.440.EQUAL.ach
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${TF} -native
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm:../avm/ -d obj ${OPTS}

all : obj all-avm
	${SC-AVM-ASM} avm/$(TARGET).avm

include ${APPLECORE}/Makefile.ac.common
