LIB=../../Lib
TF=ARTIST.OBJ
DECLS=${LIB}/ac/IO.ac:${LIB}/ac/STRING.ac:${LIB}/ac/CHAR.ac:${LIB}/ach/MON.ach:${LIB}/ach/BOOLEAN.ach:${LIB}/ac/GRAPHICS.ac:${LIB}/ac/PRINT.WORDS.ac:Include/ac/ARTIST.1.ac:Include/ac/ARTIST.2.ac
ACC = ${APPLECORE}/Compiler/bin/acc -decls=${DECLS} -tf=${TF},D1 -avm-drive=2

all-include : all
	make -C Include

clean-include : clean
	make -C Include clean

include ../../Makefile.ac.common
