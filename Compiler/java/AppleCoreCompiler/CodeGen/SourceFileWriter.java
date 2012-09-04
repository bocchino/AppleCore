/**
 * Generic pass for traversing the AST and writing out assembly code.
 * Assembler-specific syntax is handled by the Emitter class.
 */
package AppleCoreCompiler.CodeGen;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.AST.Node.RegisterExpression.Register;
import AppleCoreCompiler.AVM.*;
import AppleCoreCompiler.AVM.Instruction.*;

import java.io.*;
import java.util.*;
import java.math.*;

public class SourceFileWriter
    extends ASTScanner 
    implements Pass
{

    /* Initialization stuff */
    public final NativeCodeEmitter emitter;

    public SourceFileWriter(NativeCodeEmitter emitter) {
	this.emitter = emitter;
    }

    public void runOn(SourceFile sourceFile) 
	throws ACCError
    {
	scan(sourceFile);
    }

    /**
     * The function being processed
     */
    protected FunctionDecl currentFunction;

    public void visitSourceFile(SourceFile node) 
	throws ACCError
    {
	emitter.emitPreamble(node);
	emitter.emitSeparatorComment();
	emitter.emitComment("Assembly file generated by");
	emitter.emitComment("the AppleCore Compiler, v1.0");
	emitter.emitSeparatorComment();
	if (!node.includeMode) {
	    emitter.emitIncludeDirective("AVM.PROLOGUE.AVM");
	    FunctionDecl firstFunction = null;
	    for (Declaration decl : node.decls) {
		if (decl instanceof FunctionDecl) {
		    FunctionDecl functionDecl = (FunctionDecl) decl;
		    if (firstFunction == null && !functionDecl.isExternal) {
			firstFunction = (FunctionDecl) decl;
		    }
		}
	    }
	    if (firstFunction != null) {
		emitter.emitComment("do main function");
		emitter.emitAbsoluteInstruction("JSR",
						emitter.makeLabel(firstFunction.name));
		emitter.emitComment("exit to BASIC");
		emitter.emitAbsoluteInstruction("JMP","EXIT");
	    }
	    emitter.emitSeparatorComment();
	}
	emitter.emitComment("START OF FILE " + node.name);
	emitter.emitSeparatorComment();
	
	super.visitSourceFile(node);

	emitter.emitSeparatorComment();
	emitter.emitComment("END OF FILE " + node.name);
	emitter.emitSeparatorComment();
	if (!node.includeMode) {
	    emitter.emitEpilogue();
	}
    }

    public void visitFunctionDecl(FunctionDecl node) 
	throws ACCError
    {
	if (!node.isExternal) {
	    emitter.emitSeparatorComment();
	    emitter.emitComment("function " + node.name);
	    emitter.emitSeparatorComment();
	    for (Instruction inst : node.instructions) {
		if (!(inst instanceof LabelInstruction) &&
		    !(inst instanceof CommentInstruction)) {
		    emitter.printStream.print("\t");
		}
		emitter.printStream.println(emitter.makeLabel(inst.toString()));
	    }
	}
    }

    public void visitIncludeDecl(IncludeDecl node) {
	emitter.emitIncludeDecl(node);
    }

    public void visitDataDecl(DataDecl node) 
	throws ACCError
    {
	if (node.label != null)
	    emitter.emitLabel(node.label);
	if (node.stringConstant != null) {
	    emitter.emitStringConstant(node.stringConstant);
	    if (node.isTerminatedString) {
		emitter.emitStringTerminator();
	    }
	}
	else if (node.expr instanceof NumericConstant) {
	    NumericConstant nc = 
		(NumericConstant) node.expr;
	    emitter.emitAsData(nc);
	}
	else if (node.expr instanceof Identifier) {
	    Identifier id =
		(Identifier) node.expr;
	    emitter.emitAsData(id);
	}
	else {
	    throw new ACCInternalError("invalid data for "+node, node);
	}
    }

    public void visitVarDecl(VarDecl node) 
	throws ACCError
    {
	emitter.emitLabel(node.name);
	if (node.init == null) {
	    emitter.emitBlockStorage(node.size);
	}
	else {
	    // Previous passes must ensure cast will succeed.
	    if (!(node.init instanceof NumericConstant)) {
		throw new ACCInternalError("non-constant initializer expr for ", node);
	    }
	    NumericConstant constant = 
		(NumericConstant) node.init;
	    emitter.emitAsData(constant, node.size);
	}
    }

}
