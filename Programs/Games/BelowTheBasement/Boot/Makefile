include ${APPLECORE}/Defs.common

DECLS=${LIB-AC}/ach/MON.ach:${LIB-AC}/ac/IO.ac:${LIB-AC}/ac/DOS.ac:${LIB-AC}/ac/BANK.SWITCHES.ac
ACC += -decls=${DECLS} -tf=HELLO.OBJ

all : obj all-avm
	${SC-AVM-ASM} avm/HELLO.avm

include ${APPLECORE}/Makefile.ac.common


