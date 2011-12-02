ACC     = ../Compiler/bin/acc
ASM2SCM = ../Scripts/asm2scm
SRCS    = ${wildcard ac/*.ac}
MAKE_ALL= perl ../Scripts/make-all.pl

all-scm : scm all-linked.asm
	${MAKE_ALL} linked.asm scm

all-linked.asm : linked.asm all-asm
	${MAKE_ALL} asm linked.asm

all-asm : asm ${SRCS}
	${MAKE_ALL} ac asm

file-scm : linked.asm/${FILE}.linked.asm
	make scm/${FILE}.scm

file-linked.asm : asm/${FILE}.asm
	make linked.asm/${FILE}.linked.asm

file-asm : ac/${FILE}.ac
	make asm/${FILE}.asm

scm/${FILE}.scm : linked.asm/${FILE}.linked.asm scm 
	${ASM2SCM} < $< > $@

linked.asm/${FILE}.linked.asm : asm/${FILE}.asm linked.asm 
	echo ":NEW" > $@
	cat $< >> $@

asm/${FILE}.asm : ac/${FILE}.ac asm
	${ACC} $< > $@

scm : 
	mkdir $@

linked.asm : 
	mkdir $@

asm :
	mkdir $@

clean ::
	rm -Rf asm linked.asm scm

