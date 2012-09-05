all : FP INT
	make -C BootFP
	make -C BootINT
	make -C InstallINT
	make -C InstallFP
	make -C Editor
	cp BootFP/obj/* FP
	cp InstallFP/obj/* FP
	cp InstallINT/obj/* FP
	cp ${APPLECORE}/ROMs/ROM.77.EDITOR.OBJ FP
	cp Editor/obj/* FP
	cp BootINT/obj/* INT
	cp InstallINT/obj/* INT
	cp Editor/obj/* INT
	cp ${APPLECORE}/ROMs/ROM.77.EDITOR.OBJ INT

FP : 
	mkdir $@

INT : 
	mkdir $@

clean :
	make -C BootFP clean
	make -C BootINT clean
	make -C InstallINT clean
	make -C InstallFP clean
	make -C Editor clean
	rm -Rf FP INT

