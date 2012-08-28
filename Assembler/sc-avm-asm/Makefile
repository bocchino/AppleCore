TARGET=sc-avm-asm
DIR=${APPLECORE}/Assembler/sc-avm-asm
SIG=${wildcard sig/*.sig}
SML=${wildcard sml/*.sml}
ML_RUN=${DIR}/ml-run

all : ${TARGET}.x86-darwin

${TARGET}.x86-darwin : Assembler.cm ${SIG} ${SML}
	ml-build Assembler.cm Assembler.main ${TARGET}

clean ::
	rm -f ${TARGET}.x86-darwin
