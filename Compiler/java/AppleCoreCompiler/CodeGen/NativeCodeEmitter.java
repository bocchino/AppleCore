/**
 * Class for writing out 6502 assembly instructions.  This class
 * factors out assembler-independent syntax details.  To write code
 * for a specific assembler, implement this pass and provide the
 * assembler-specific functions.
 */
package AppleCoreCompiler.CodeGen;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.AST.Node.RegisterExpression.Register;

import java.io.*;
import java.util.*;
import java.math.*;

public abstract class NativeCodeEmitter
{
    public final PrintStream printStream;
    public NativeCodeEmitter(PrintStream printStream) {
	this.printStream = printStream;
    }

    public void emitLine() {
	printStream.println();
    }

    public void emit(String s) {
	printStream.print(s);
    }

    public void emit(char ch) {
	printStream.print(ch);
    }

    public abstract void emitIncludeDecl(IncludeDecl node);

    public abstract String addrString(int addr);

    public abstract void emitInstruction(String s);

    public abstract void emitAbsoluteInstruction(String mnemonic, 
						 int addr);

    public abstract void emitAbsoluteInstruction(String mnemonic, 
						 String label);

    public abstract void emitImmediateInstruction(String mnemonic, 
						  String imm, boolean high);

    public abstract void emitImmediateInstruction(String mnemonic, int imm);

    public void emitIndirectYInstruction(String mnemonic, int addr) {
	emitIndirectYInstruction(mnemonic, addrString(addr));
    }

    public abstract void emitIndirectYInstruction(String mnemonic, String addr);

    public abstract void emitIndirectXInstruction(String mnemonic, int addr);

    public abstract void emitIndexedInstruction(String mnemonic, 
						int addr, String reg);

    public abstract void emitAbsoluteXInstruction(String mnemonic, String addr);

    public abstract void emitComment(String comment);

    public void emitComment(Object o) { emitComment(o.toString()); }

    public abstract void emitSeparatorComment();

    public void emitLabel(String label) {
	emit(makeLabel(label));
	emitLine();
    }


    public abstract void emitStringTerminator();

    public abstract void emitBlockStorage(int nbytes);

    public abstract void emitStringConstant(StringConstant sc);

    public abstract String makeLabel(String label);

    public abstract void emitPreamble(SourceFile node);

    public abstract void emitEpilogue();

    public abstract void emitIncludeDirective(String fileName);

    public abstract void emitExpression(Expression expr) throws ACCError;

    /**
     * Emit expression with a size bound, for initializing global
     * variables.
     */
    public abstract void emitSizedExpression(Expression expr,
					     int size) throws ACCError;

}
