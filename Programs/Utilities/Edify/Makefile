include ${APPLECORE}/Defs.common

INCLUDE=./Include
OBJ=EDIFY.OBJ

DECLS=${INCLUDE}/ac/EDIFY.INPUT.ac:${INCLUDE}/ac/EDIFY.BUFFER.ac:${LIB-AC}/ac/IO.ac:${LIB-AC}/ac/STRING.ac:${LIB-AC}/ac/CHAR.ac:${LIB-AC}/ac/COPY.ac:${LIB-AC}/ach/MON.ach:${LIB-AC}/ach/BOOLEAN.ach

ACC += -decls=${DECLS} -tf=${OBJ}

SC-AVM-ASM += -i Include/avm

all-include : obj all-avm
	make -C Include
	${SC-AVM-ASM} avm/EDIFY.avm

include ${APPLECORE}/Makefile.ac.common

CLEAN=make -C Include clean

