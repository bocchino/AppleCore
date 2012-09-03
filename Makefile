all :
	make -C Compiler
	make -C Assembler/sc-avm-asm
	make -C Lib
	make -C Lib-AVM
	make -C Runtime
	make -C Examples

clean :
	make -C Compiler clean
	make -C Assembler/sc-avm-asm clean
	make -C Lib clean
	make -C Lib-AVM clean
	make -C Runtime clean
	make -C Examples clean
