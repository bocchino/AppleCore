LIB=../../../Lib
TF=RODS.COLOR.PATTERN.OBJ
DECLS=${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach:${LIB}/ac/GRAPHICS.ac:${LIB}/ac/PRINT.WORDS.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${TF}
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm -d obj ${OPTS}

all : obj all-avm
	${SC-AVM-ASM} avm/RODS.COLOR.PATTERN.avm

include ../../../Makefile.ac.common
