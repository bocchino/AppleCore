include ${APPLECORE}/Defs.common

TF=ARTIST.OBJ

DECLS=${LIB-AC}/ac/IO.ac:${LIB-AC}/ac/STRING.ac:${LIB-AC}/ac/CHAR.ac:${LIB-AC}/ach/MON.ach:${LIB-AC}/ach/BOOLEAN.ach:${LIB-AC}/ac/GRAPHICS.ac:${LIB-AC}/ac/PRINT.WORDS.ac:Include/ac/ARTIST.1.ac:Include/ac/ARTIST.2.ac

ACC += -decls=${DECLS} -tf=${TF}

SC-AVM-ASM += -i Include/avm

all-include : obj all-avm
	make -C Include
	${SC-AVM-ASM} avm/ARTIST.avm

CLEAN= make -C Include clean

include ${APPLECORE}/Makefile.ac.common
