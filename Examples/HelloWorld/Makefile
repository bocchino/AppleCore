LIB=../../Lib
DECLS=${LIB}/ac/IO.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=HELLO.WORLD.OBJ,D1 -avm-drive=2
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm -d obj ${OPTS}

all : obj all-avm
	${SC-AVM-ASM} avm/HELLO.WORLD.avm

include ../../Makefile.ac.common.2
