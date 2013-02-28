include ${APPLECORE}/Defs.common

INCLUDE=Include

DECLS=${INCLUDE}/ac/MA.EVENTS.ac:${INCLUDE}/ac/MA.PARSING.ac:${INCLUDE}/ac/MA.ROOMS.ac:${INCLUDE}/ac/MA.OBJECTS.1.ac:${INCLUDE}/ac/MA.OBJECTS.2.ac:${INCLUDE}/ac/MA.STATE.ac:${LIB-AC}/ac/IO.ac:${LIB-AC}/ac/STRING.ac:${LIB-AC}/ac/CHAR.ac:${LIB-AC}/ach/MON.ach:${LIB-AC}/ach/BOOLEAN.ach:${LIB-AC}/ac/PRINT.WORDS.ac

ACC += -decls=${DECLS} -tf=MINI.ADVENTURE.OBJ

SC-AVM-ASM += -i Include/avm

all-include : obj all-avm
	make -C Include
	${SC-AVM-ASM} avm/MINI.ADVENTURE.avm

CLEAN=make -C Include clean

include ${APPLECORE}/Makefile.ac.common

