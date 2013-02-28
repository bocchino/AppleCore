include ${APPLECORE}/Defs.common

TF=HEXCONVERT.OBJ
DECLS=${LIB-AC}/ac/IO.ac:${LIB-AC}/ac/STRING.ac:${LIB-AC}/ac/CHAR.ac:${LIB-AC}/ach/MON.ach:${LIB-AC}/ach/BOOLEAN.ach

ACC += -decls=${DECLS} -tf=${TF}

all : obj all-avm
	${SC-AVM-ASM} avm/HEXCONVERT.avm

include ${APPLECORE}/Makefile.ac.common
