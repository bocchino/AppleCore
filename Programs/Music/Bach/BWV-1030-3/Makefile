include ${APPLECORE}/Defs.common

TARGET=BWV.1030.3
TF=$(TARGET).OBJ

DECLS=${LIB-AC}/ach/MON.ach:${LIB-AC}/ac/IO.ac:${LIB-AC}/ac/STRING.ac:${LIB-AC}/ach/BOOLEAN.ach:${LIB-AC}/ac/GRAPHICS.ac:${LIB-AC}/ac/TONES.ac:${LIB-AC}/ach/PITCHES.440.EQUAL.ach

ACC += -decls=${DECLS} -tf=${TF}

all : obj all-avm
	${SC-AVM-ASM} avm/$(TARGET).avm

include ${APPLECORE}/Makefile.ac.common
