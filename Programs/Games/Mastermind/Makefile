include ${APPLECORE}/Defs.common

TF=MASTERMIND.OBJ
DECLS=${LIB-AC}/ac/IO.ac:${LIB-AC}/ac/STRING.ac:${LIB-AC}/ac/CHAR.ac:${LIB-AC}/ach/MON.ach:${LIB-AC}/ach/BOOLEAN.ach:${LIB-AC}/ac/GRAPHICS.ac:${LIB-AC}/ac/PRINT.WORDS.ac:Include/ac/MASTERMIND.INCLUDE.ac:
ACC += -decls=${DECLS} -tf=${TF}
SC-AVM-ASM += -i Include/avm

all-include : obj all-avm
	make -C Include
	${SC-AVM-ASM} avm/MASTERMIND.avm

CLEAN= make -C Include clean

include ${APPLECORE}/Makefile.ac.common
