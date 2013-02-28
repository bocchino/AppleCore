all : BTB
	make -C ${APPLECORE}/ShellEditor
	cp ${APPLECORE}/ROMs/ROM.77.EDITOR.OBJ BTB
	cp ${APPLECORE}/ShellEditor/INT/* BTB
	make -C Boot
	make -C AppleCore
	make -C Assembly
	cp Boot/obj/* BTB
	cp Assembly/obj/* BTB

BTB :
	mkdir $@

clean :
	make -C Boot clean
	make -C AppleCore clean
	make -C Assembly clean
	rm -Rf BTB