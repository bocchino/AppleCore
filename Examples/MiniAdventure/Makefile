LIB=../../Lib
INCLUDE=Include
DECLS=${INCLUDE}/ac/MA.EVENTS.ac:${INCLUDE}/ac/MA.PARSING.ac:${INCLUDE}/ac/MA.ROOMS.ac:${INCLUDE}/ac/MA.OBJECTS.1.ac:${INCLUDE}/ac/MA.OBJECTS.2.ac:${INCLUDE}/ac/MA.STATE.ac:${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach:${LIB}/ac/PRINT.WORDS.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=MINI.ADVENTURE.OBJ,D1 -avm-drive=2

all-include : all
	make -C Include

clean-include : clean
	make -C Include clean

include ../../Makefile.ac.common
