all : BTB
	make -C ${APPLECORE}/ShellEditor
	cp ${APPLECORE}/ROMs/ROM.77.EDITOR.OBJ BTB
	cp ${APPLECORE}/ShellEditor/INT/* BTB
	make -C Boot
	make -C BTB-AC
	make -C BTB-AVM
	cp Boot/obj/* BTB
	cp BTB-AVM/obj/* BTB

BTB :
	mkdir $@

clean :
	make -C Boot clean
	make -C BTB-AC clean
	make -C BTB-AVM clean
	rm -Rf BTB