EDITOR-ROM=$(APPLECORE)/ROMs/ROM.77.EDITOR.OBJ
EDITOR-MON=$(APPLECORE)/ROMS/MON.77.EDITOR.OBJ

all : INT FP

INT : BootINT/obj InstallINT/obj Editor/obj $(EDITOR-ROM)
	rm -Rf $@
	mkdir $@
	cp BootINT/obj/* $@
	cp InstallINT/obj/* $@
	cp Editor/obj/* $@
	cp $(EDITOR-ROM) $@

FP : BootFP/obj InstallFP/obj Editor/obj $(EDITOR-ROM) $(EDITOR-MON)
	rm -Rf $@
	mkdir $@
	cp BootFP/obj/* $@
	cp InstallFP/obj/* $@
	cp Editor/obj/* $@
	cp $(EDITOR-ROM) $@
	cp $(EDITOR-MON) $@

BootINT/obj :
	make -C BootInt

InstallINT/obj :
	make -C InstallINT

BootFP/obj :
	make -C BootFP

InstallFP/obj :
	make -C InstallFP

Editor/obj :
	make -C Editor

clean :
	make -C BootFP clean
	make -C BootINT clean
	make -C InstallINT clean
	make -C InstallFP clean
	make -C Editor clean
	rm -Rf FP INT

