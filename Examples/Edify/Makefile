LIB=../../Lib
INCLUDE=./Include
OBJ=EDIFY.OBJ
DECLS=${INCLUDE}/ac/EDIFY.1.ac:${INCLUDE}/ac/EDIFY.BUFFER.ac:${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${OBJ},D1 -avm-drive=2

all-include : all
	make -C Include

clean-include : clean
	make -C Include clean

include ../../Makefile.ac.common
