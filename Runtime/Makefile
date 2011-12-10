ASM2SCM = ../Scripts/asm2scm
SRCS    = ${wildcard asm/*.asm}
MAKE_ALL= perl ../Scripts/make-all.pl

all-scm : scm ${SRCS}
	${MAKE_ALL} asm scm

file-scm : asm/${FILE}.asm
	make scm/${FILE}.scm

scm/${FILE}.scm : asm/${FILE}.asm scm 
	${ASM2SCM} < $< > $@

scm : 
	mkdir $@

clean ::
	rm -Rf scm
