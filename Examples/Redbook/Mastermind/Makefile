LIB=../../../Lib
TF=MASTERMIND.OBJ
DECLS=${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach:${LIB}/ac/GRAPHICS.ac:${LIB}/ac/PRINT.WORDS.ac:Include/ac/MASTERMIND.1.ac:Include/ac/MASTERMIND.2.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${TF}
SC-AVM-ASM = ${APPLECORE}/Assembler/bin/sc-avm-asm -i ${APPLECORE}/Lib/avm:${APPLECORE}/Lib-AVM/avm:${APPLECORE}/Runtime/avm:Include/avm -d obj ${OPTS}

all-include : obj all-avm
	make -C Include
	${SC-AVM-ASM} avm/MASTERMIND.avm

clean-include : clean
	make -C Include clean

include ../../../Makefile.ac.common.2
