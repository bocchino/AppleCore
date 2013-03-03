BTB : $(APPLECORE)/ROMs/ROM.77.EDITOR.OBJ $(APPLECORE)/ShellEditor/INT Boot/obj AppleCore/avm Assembly/obj
	rm -Rf $@
	mkdir $@
	cp $(APPLECORE)/ROMs/ROM.77.EDITOR.OBJ $@
	cp $(APPLECORE)/ShellEditor/INT/* $@
	cp Boot/obj/* $@
	cp Assembly/obj/* $@

$(APPLECORE)/ShellEditor/INT :
	make -C $(APPLECORE)/ShellEditor

Boot/obj :
	make -C Boot

AppleCore/avm :
	make -C AppleCore

Assembly/obj :
	make -C Assembly

clean :
	make -C Boot clean
	make -C AppleCore clean
	make -C Assembly clean
	rm -Rf BTB

.PHONY: clean
