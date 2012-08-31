LIB=../../Lib
INCLUDE=./Include
OBJ=EDIFY.OBJ
DECLS=${INCLUDE}/ac/EDIFY.1.ac:${INCLUDE}/ac/EDIFY.BUFFER.1.ac:${INCLUDE}/ac/EDIFY.BUFFER.2.ac:${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ac/COPY.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${OBJ},D1 -avm-drive=2

all-include : all
	make -C Include

clean-include : clean
	make -C Include clean

include ../../Makefile.ac.common

SC-AVM-ASM += -i Include/avm -l
