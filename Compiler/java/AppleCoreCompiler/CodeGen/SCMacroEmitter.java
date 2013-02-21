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

public class SCMacroEmitter
    extends NativeCodeEmitter
{

    public SCMacroEmitter(PrintStream printStream) {
	super(printStream);
    }

    public void emitIncludeDecl(IncludeDecl node) {
	emitAbsoluteInstruction(".IN",node.filename);
    }

    /* Emitter methods */

    public String addrString(int addr) {
	return "$" + Integer.toString(addr,16).toUpperCase();
    }

    public void emitInstruction(String s) {
	emit("\t" + s + "\n");
    }

    public void emitAbsoluteInstruction(String mnemonic, int addr) {
	emitInstruction(mnemonic + " " + addrString(addr));
    }

    public void emitAbsoluteInstruction(String mnemonic, String label) {
	emitInstruction(mnemonic + " " + makeLabel(label));
    }

    public void emitImmediateInstruction(String mnemonic, String imm, boolean high) {
	String marker = high ? " /" : " #";
	emitInstruction(mnemonic + marker + makeLabel(imm));
    }

    public void emitImmediateInstruction(String mnemonic, int imm) {
	emitInstruction(mnemonic + " #" + addrString(imm));
    }

    public void emitIndirectYInstruction(String mnemonic, String addr) {
	emitInstruction(mnemonic + " (" + addr + "),Y");
    }

    public void emitIndirectXInstruction(String mnemonic, int addr) {
	emitInstruction(mnemonic + " (" + addrString(addr) + ",X)");
    }

    public void emitAbsoluteXInstruction(String mnemonic, String addr) {
	emitInstruction(mnemonic + addr + ",X");
    }

    public void emitIndexedInstruction(String mnemonic, int addr, String reg) {
	emitInstruction(mnemonic + " " + addrString(addr) + "," + reg);
    }

    public void emitComment(String comment) {
	emit("* ");
	emit(comment.toUpperCase() + "\n");
    }

    public void emitSeparatorComment() {
	emitComment("-------------------------------");
    }

    public String makeLabel(String label) {
	// S-C Macro Assembler doesn't like underscores in labels
	return label.replace('_','.').toUpperCase();
    }

    private boolean isPrintable(char ch) {
	return (ch != '\"') && (ch >= 32 && ch <= 126);
    }

    /**
     * Emit a string constant as
     * - .AS "XXX" for the printable chars
     * - .HS XX    for the non-printable chars and quotes
     */
    public void emitStringConstant(StringConstant sc) {
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

    public void emitStringTerminator() {
	emitAbsoluteInstruction(".HS","00");
    }

    public void emitBlockStorage(int nbytes) {
	emit("\t.BS ");
	emit(addrString(nbytes));
	emit("\n");
    }

    private String byteAsHexString(int byteValue) {
	String byteString = 
	    Integer.toString(byteValue,16).toUpperCase();
	return (byteString.length() == 2) ? 
	    byteString : "0"+byteString;
    }

    public void emitPreamble(SourceFile node) {
	if (node.origin > 0) {
	    emitAbsoluteInstruction(".OR", node.origin);
        }
	else if (!node.includeMode) {
	    // Default origin
	    emitAbsoluteInstruction(".OR", 0x803);
	}
	if (!node.includeMode) {
	    emitAbsoluteInstruction(".TF",node.targetFile);
	}
    }

    public void emitEpilogue() {
        emitIncludeDirective("AVM.AVM");
         // Start of program stack
        emitLabel("AVM.STACK");
    }

    public void emitIncludeDirective(String fileName) {
	emitAbsoluteInstruction(".IN", fileName);
    }

    public void emitExpression(Expression expr) 
	throws ACCError
    {
	new ExpressionEmitter(expr).emitExpression();
    }

    private class ExpressionEmitter
	extends NodeVisitor
    {

	private Expression expr;

	public ExpressionEmitter(Expression expr) {
	    this.expr = expr;
	}

	protected boolean succeeded;

	public void emitExpression()
	    throws ACCError
	{
	    if (expr == null) throw new ACCInternalError();
	    expr.accept(this);
	}

	@Override
	public void visitIntegerConstant(IntegerConstant expr)
	    throws ACCError
	{
	    emit("\t.HS ");
	    int size = expr.getSize();
	    for (int i = 0; i < size; ++i) {
		emit(byteAsHexString(expr.valueAtIndex(i)).toUpperCase());
	    }
	    emit("\n");
	}

	@Override
	public void visitCharConstant(CharConstant expr)
	    throws ACCError
	{
	    emitAbsoluteInstruction(".DA","#'"+expr.value+"'");
	}

	@Override
	public void visitIdentifier(Identifier id)
	    throws ACCError
	{
	    emitAbsoluteInstruction(".DA",makeLabel(id.name));
	}

	@Override
	public void visitNode(Node node) 
	    throws ACCError
	{
	    throw new ACCInternalError();
	}

    }

    public void emitSizedExpression(Expression expr, int size) 
	throws ACCError
    {
	new SizedExpressionEmitter(expr, size).emitExpression();
    }

    private class SizedExpressionEmitter
	extends ExpressionEmitter
    {
	private int size;

	public SizedExpressionEmitter(Expression expr, int size) {
	    super(expr);
	    this.size = size;
	}

	@Override
	public void visitIntegerConstant(IntegerConstant expr)
	    throws ACCError
	{
	    int constSize = expr.getSize();
	    int dataSize = constSize <= size ? 
		constSize : size;
	    emit("\t.HS ");
	    for (int i = 0; i < dataSize; ++i) {
		emit(byteAsHexString(expr.valueAtIndex(i)).toUpperCase());
	    }
	    emit("\n");
	    if (size > constSize) {
		emit("\t.HS ");
		for (int i = constSize; i < size; ++i) {
		    emit("00");
		}
		emit("\n");
	    }
	}

    }

}
