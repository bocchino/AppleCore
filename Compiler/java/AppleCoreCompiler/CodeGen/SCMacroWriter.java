/**
 * Write out assembly code for the S-C Macro Assembler.
 */
package AppleCoreCompiler.CodeGen;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.AST.Node.RegisterExpression.Register;

import java.io.*;
import java.math.*;

public class SCMacroWriter
    extends AssemblyWriter
{

    public SCMacroWriter(PrintStream printStream) {
	super(printStream);
    }

    /* Emitter methods */

    protected String addrString(int addr) {
	return "$" + Integer.toString(addr,16).toUpperCase();
    }

    protected void emitInstruction(String s) {
	emit("\t" + s + "\n");
    }

    protected void emitAbsoluteInstruction(String mnemonic, int addr) {
	emitInstruction(mnemonic + " " + addrString(addr));
    }

    protected void emitAbsoluteInstruction(String mnemonic, String label) {
	emitInstruction(mnemonic + " " + label);
    }

    protected void emitImmediateInstruction(String mnemonic, String imm, boolean high) {
	String marker = high ? " /" : " #";
	emitInstruction(mnemonic + marker + imm);
    }

    protected void emitImmediateInstruction(String mnemonic, int imm) {
	emitInstruction(mnemonic + " #" + addrString(imm));
    }

    protected void emitIndirectYInstruction(String mnemonic, String addr) {
	emitInstruction(mnemonic + " (" + addr + "),Y");
    }

    protected void emitIndirectXInstruction(String mnemonic, int addr) {
	emitInstruction(mnemonic + " (" + addrString(addr) + ",X)");
    }

    protected void emitAbsoluteXInstruction(String mnemonic, String addr) {
	emitInstruction(mnemonic + addr + ",X");
    }

    protected void emitIndexedInstruction(String mnemonic, int addr, String reg) {
	emitInstruction(mnemonic + " " + addrString(addr) + "," + reg);
    }

    protected void emitComment(String comment) {
	emit("* ");
	for (int i = 0; i < commentIndent; ++i)
	    emit(" ");
	emit(comment.toUpperCase() + "\n");
    }

    protected void emitSeparatorComment() {
	emitComment("-------------------------------");
    }

    protected String labelAsString(String label) {
	// S-C Macro Assembler doesn't like underscores in labels
	return label.replace('_','.').toUpperCase();
    }

    private boolean isPrintable(char ch) {
	return ch >= 32 && ch <= 126;
    }

    /**
     * Emit a string constant as
     * - .AS "XXX" for the printable chars
     * - .HS XX    for the non-printable chars
     */
    protected void emitStringConstant(StringConstant sc) {
	String s = sc.value;
	int pos = 0;
	while (pos < s.length()) {
	    if (isPrintable(s.charAt(pos))) {
		emit("\t.AS \"");
		while (pos < s.length() &&
		       isPrintable(s.charAt(pos))) {
		    emit(s.charAt(pos++));
		}
		emit("\"\n");
	    }
	    else {
		emitAbsoluteInstruction(".HS",
					byteAsHexString(s.charAt(pos++)));
	    }
	}
    }

    protected void emitStringTerminator() {
	emitAbsoluteInstruction(".HS","00");
    }

    protected void emitAsData(NumericConstant c) {
	if (c instanceof IntegerConstant) {
	    IntegerConstant intConst = (IntegerConstant) c;
	    emit("\t.HS ");
	    int size = intConst.getSize();
	    for (int i = 0; i < size; ++i) {
		emit(byteAsHexString(intConst.valueAtIndex(i)).toUpperCase());
	    }
	    emit("\n");
	}
	else {
	    CharConstant charConst = (CharConstant) c;
	    emitAbsoluteInstruction(".DA","'"+charConst.value+"'");
	}

    }

    protected void emitAsData(NumericConstant c, int sizeBound) {
	if (c instanceof IntegerConstant) {
	    IntegerConstant intConst = (IntegerConstant) c;
	    int constSize = intConst.getSize();
	    int dataSize = constSize <= sizeBound ? 
		constSize : sizeBound;
	    emit("\t.HS ");
	    for (int i = 0; i < dataSize; ++i) {
		emit(byteAsHexString(intConst.valueAtIndex(i)).toUpperCase());
	    }
	    emit("\n");
	    if (sizeBound > constSize) {
		emit("\t.HS ");
		for (int i = constSize; i < sizeBound; ++i) {
		    emit("00");
		}
		emit("\n");
	    }
	}
	else {
	    emitAsData(c);
	}
    }

    protected void emitBlockStorage(int nbytes) {
	emit("\t.BS\t");
	emit(addrString(nbytes));
	emit("\n");
    }

    private String byteAsHexString(int byteValue) {
	String byteString = 
	    Integer.toString(byteValue,16).toUpperCase();
	return (byteString.length() == 2) ? 
	    byteString : "0"+byteString;
    }

}
