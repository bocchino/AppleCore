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

    public void emitSizedExpression(Expression expr, int size) 
	throws ACCError
    {
	new ExpressionEmitter(expr, size).emitExpression();
    }

    /**
     * Visitor class for emitting an expression as SCMASM data
     */
    private class ExpressionEmitter
	extends NodeVisitor
    {

	/**
	 * The expression to emit
	 */
	private final Expression expr;

	/**
	 * The size of the emitted data before any padding
	 */
	private final int dataSize;

	/**
	 * Size of padding
	 */
	private final int paddingSize;

	public ExpressionEmitter(Expression expr, 
				 int targetSize) {
	    this.expr = expr;
	    this.dataSize = (expr.size > targetSize) ?
		targetSize : expr.size;
	    this.paddingSize = (expr.size < targetSize) ?
		targetSize - expr.size : 0;
	}

	public ExpressionEmitter(Expression expr) {
	    this(expr,expr.size);
	}

	public void emitExpression()
	    throws ACCError
	{
	    if (expr == null) throw new ACCInternalError();
	    expr.accept(this);
	}

	private void emitPadding(boolean emitOnes) {
	    if (paddingSize > 0) {
		emit("\t.HS ");
		for (int i = 0; i < paddingSize; ++i) {
		    emit(emitOnes ? "FF" : "00");
		}
		emit("\n");
	    }
	}

	@Override
	public void visitIntegerConstant(IntegerConstant expr)
	    throws ACCError
	{
	    emit("\t.HS ");
	    for (int i = 0; i < dataSize; ++i) {
		emit(byteAsHexString(expr.valueAtIndex(i)).toUpperCase());
	    }
	    emit("\n");
	    boolean emitOnes = expr.isSigned && 
		((expr.valueAtIndex(dataSize-1) & 0x80) != 0);
	    emitPadding(emitOnes);
	}

	@Override
	public void visitCharConstant(CharConstant expr)
	    throws ACCError
	{
	    emitAbsoluteInstruction(".DA","#'"+expr.value+"'");
	    emitPadding(false);
	}

	@Override
	public void visitIdentifier(Identifier expr)
	    throws ACCError
	{
	    if (dataSize == 1) {
		emitAbsoluteInstruction(".DA","#"+makeLabel(expr.name));
	    }
	    else {
		emitAbsoluteInstruction(".DA",makeLabel(expr.name));
	    }
	    emitPadding(false);
	}

	@Override
	public void visitUnopExpression(UnopExpression unop) 
	    throws ACCError
	{
	    switch (unop.operator) {
	    case ADDRESS:
		if (unop.expr instanceof Identifier) {
		    unop.expr.accept(this);
		    emitPadding(false);
		}
		else {
		    throw new ACCInternalError();
		}
		break;
	    default:
		throw new ACCInternalError();
	    }
	}

	@Override
	public void visitNode(Node node) 
	    throws ACCError
	{
	    throw new ACCInternalError();
	}

    }

}
