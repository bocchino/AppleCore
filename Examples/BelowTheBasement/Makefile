all : BTB
	make ${APPLECORE}/ShellEditor
	cp ${APPLECORE}/ROMs/ROM.77.EDITOR.OBJ BTB
	cp ${APPLECORE}/ShellEditor/INT/* BTB
	make -C Boot
	make -C ac
	make -C avm
	cp Boot/obj/* BTB
	cp avm/obj/* BTB

BTB :
	mkdir $@

clean :
	make -C Boot clean
	make -C ac clean
	make -C avm clean
	rm -Rf BTB