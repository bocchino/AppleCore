LIB=${APPLECORE}/Lib
TF=ARTIST.OBJ
DECLS=${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach:${LIB}/ac/GRAPHICS.ac:${LIB}/ac/PRINT.WORDS.ac:Include/ac/ARTIST.1.ac:Include/ac/ARTIST.2.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${TF}
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm:Include/avm -d obj ${OPTS}

all-include : obj all-avm
	make -C Include
	${SC-AVM-ASM} avm/ARTIST.avm

CLEAN= make -C Include clean

include ${APPLECORE}/Makefile.ac.common
