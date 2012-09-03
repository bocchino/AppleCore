AVM-DIR=AVM-Spec
AVM-FILE=AVM-Spec-v1.0.pdf
AC-DIR=AppleCore-Spec
AC-FILE=AppleCore-Spec-v1.0.pdf

all :
	make -C ${AVM-DIR}
	make -C ${AC-DIR}
	cp ${AVM-DIR}/${AVM-FILE} ../${AVM-FILE}
	cp ${AC-DIR}/${AC-FILE} ../${AC-FILE}

clean :
	make -C ${AVM-DIR} clean
	make -C ${AC-DIR} clean
