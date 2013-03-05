# -------------------------------------
# MAKEFILE FOR THE APPLECORE COMPILER
# VERSION 1.0
# -------------------------------------

SRCS=\
${wildcard java/AppleCoreCompiler/Compiler/*java} \
${wildcard java/AppleCoreCompiler/AST/*.java} \
${wildcard java/AppleCoreCompiler/Syntax/*.java} \
${wildcard java/AppleCoreCompiler/Semantics/*.java} \
${wildcard java/AppleCoreCompiler/Transforms/*.java} \
${wildcard java/AppleCoreCompiler/AVM/*.java} \
${wildcard java/AppleCoreCompiler/CodeGen/*java} \
${wildcard java/AppleCoreCompiler/Errors/*java} \
${wildcard java/AppleCoreCompiler/Warnings/*java} \

classes : ${SRCS}
	rm -rf $@
	mkdir $@
	javac -d $@ ${SRCS}

clean :
	rm -rf classes

.PHONY : clean
