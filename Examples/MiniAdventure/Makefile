LIB=../../Lib
INCLUDE=Include
DECLS=${INCLUDE}/ac/MA.EVENTS.ac:${INCLUDE}/ac/MA.PARSING.ac:${INCLUDE}/ac/MA.ROOMS.ac:${INCLUDE}/ac/MA.OBJECTS.1.ac:${INCLUDE}/ac/MA.OBJECTS.2.ac:${INCLUDE}/ac/MA.STATE.ac:${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach:${LIB}/ac/PRINT.WORDS.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=MINI.ADVENTURE.OBJ
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm:Include/avm -d obj ${OPTS}

all-include : obj all-avm
	make -C Include
	${SC-AVM-ASM} avm/MINI.ADVENTURE.avm

CLEAN=make -C Include clean

include ../../Makefile.ac.common

