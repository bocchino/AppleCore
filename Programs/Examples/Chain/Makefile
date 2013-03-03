all : Assembly/obj

Assembly/obj : AppleCore/avm
	make -C Assembly

AppleCore/avm :
	make -C AppleCore

clean :
	make -C AppleCore clean
	make -C Assembly clean
