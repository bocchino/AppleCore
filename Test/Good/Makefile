include ${APPLECORE}/Defs.common

DECLS=${LIB-AC}/ac/IO.ac:${LIB-AC}/ach/MON.ach:${LIB-AC}/ach/BOOLEAN.ach:${LIB-AC}/ac/GRAPHICS.ac:${LIB-AC}/ac/STRING.ac

ACC += -decls=${DECLS}

all : obj all-avm
	${MAKE_ALL} avm obj

obj/${FILE}.obj : avm/${FILE}.avm obj
	${SC-AVM-ASM} avm/${FILE}.avm

include ${APPLECORE}/Makefile.ac.common
