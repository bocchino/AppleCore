LIB=../../Lib
INCLUDE=./Include
OBJ=EDIFY.OBJ
DECLS=${INCLUDE}/ac/EDIFY.1.ac:${INCLUDE}/ac/EDIFY.BUFFER.1.ac:${INCLUDE}/ac/EDIFY.BUFFER.2.ac:${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ac/COPY.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${OBJ}
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm:Include/avm -d obj ${OPTS}

all-include : obj all-avm
	make -C Include
	${SC-AVM-ASM} avm/EDIFY.avm

clean-include : clean
	make -C Include clean

include ../../Makefile.ac.common
