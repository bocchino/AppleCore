# -------------------------------------
# MAKEFILE FOR THE APPLECORE COMPILER
# VERSION 1.0
# -------------------------------------

SRCS=\
${wildcard java/AppleCoreCompiler/Compiler/*java} \
${wildcard java/AppleCoreCompiler/Syntax/*.java} \
${wildcard java/AppleCoreCompiler/Semantics/*.java} \
${wildcard java/AppleCoreCompiler/CodeGen/*java} \
${wildcard java/AppleCoreCompiler/AST/*.java} \
${wildcard java/AppleCoreCompiler/Errors/*java} \
${wildcard java/AppleCoreCompiler/Warnings/*java} \

all : classes ${SRCS}
	javac -d classes ${SRCS}

classes :
	mkdir classes

clean ::
	rm -rf classes
