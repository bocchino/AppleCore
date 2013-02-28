include ${APPLECORE}/Defs.common

TF=RODS.COLOR.PATTERN.OBJ

DECLS=${LIB-AC}/ac/IO.ac:${LIB-AC}/ac/STRING.ac:${LIB-AC}/ac/CHAR.ac:${LIB-AC}/ach/MON.ach:${LIB-AC}/ach/BOOLEAN.ach:${LIB-AC}/ac/GRAPHICS.ac:${LIB-AC}/ac/PRINT.WORDS.ac:${LIB-AC}/ach/SOFT.SWITCHES.ach

ACC += -decls=${DECLS} -tf=${TF}

SC-AVM-ASM += -i ../avm

all : obj all-avm
	${SC-AVM-ASM} avm/RODS.COLOR.PATTERN.avm

include ${APPLECORE}/Makefile.ac.common
