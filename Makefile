all :
	make -C Compiler
	make -C Assembler/sc-avm-asm
	make -C Lib
	make -C Examples
	make -C ShellEditor

clean :
	make -C Compiler clean
	make -C Assembler/sc-avm-asm clean
	make -C Lib clean
	make -C Examples clean
	make -C ShellEditor clean

