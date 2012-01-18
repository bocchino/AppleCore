/**
 * Generate AVM instructions for all functions in the source file.
 */
package AppleCoreCompiler.AVM;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.AST.Node.RegisterExpression.Register;
import AppleCoreCompiler.AVM.Instruction.*;

import java.io.*;
import java.util.*;
import java.math.*;

public class AVMTranslatorPass
    extends ASTScanner 
    implements Pass
{

    public void runOn(SourceFile sourceFile) 
	throws ACCError
    {
	for (Declaration decl : sourceFile.decls) {
	    if (decl instanceof FunctionDecl) {
		scan(decl);
	    }
	}
    }

    /* Constants */

    /**
     * Size of saved info on stack
     */
    public static final int SAVED_INFO_SIZE=6;

    /* State variables for tree traversal */

    /**
     * The function being processed
     */
    private FunctionDecl currentFunction;

    /**
     * Counter for branch labels
     */
    private int branchLabelCount = 1;

    /**
     * Counter for constants
     */
    private int constCount = 1;

    /**
     * Whether a variable seen is being evaluated for its address or
     * its value.
     */
    private boolean needAddress;

    /**
     * Are we in debug mode?
     */
    public boolean debug = false;

    /* Visitor methods */

    /* Leaf nodes */

    public void visitIntegerConstant(IntegerConstant node) {
	emit(new PHCInstruction(node));    
    }

    public void visitCharConstant(CharConstant node) {
	emit(new PHCInstruction(node));
    }

    public void visitIdentifier(Identifier node) 
	throws ACCError 
    {
	Node def = node.def;
	if (def instanceof VarDecl) {
	    VarDecl varDecl = (VarDecl) def;
	    // Push variable's address on stack
	    if (varDecl.isLocalVariable) {
		emit(new PVAInstruction(varDecl.getOffset()));
	    }
	    else {
		emit(new PHCInstruction(new Address(varDecl.name)));
	    }
	    if (!needAddress) {
		// Use the address to get the value.
		emit(new MTSInstruction(node.size));
	    }
	}
	else if (def instanceof ConstDecl) {
	    ConstDecl cd = (ConstDecl) def;
	    scan(cd.expr);
	}
	else if (def instanceof DataDecl) {
	    DataDecl dataDecl = (DataDecl) def;
	    emit(new PHCInstruction(new Address(dataDecl.label)));
	}
	else if (def instanceof FunctionDecl) {
	    FunctionDecl fnDecl = (FunctionDecl) def;
	    emit(new PHCInstruction(new Address(fnDecl.name)));
	}
    }

    /* Non-leaf nodes */

    public void visitFunctionDecl(FunctionDecl node) 
	throws ACCError
    {
	if (!node.isExternal) {

	    currentFunction = node;
	    branchLabelCount=1;

	    computeStackSlotsFor(node);
	    
	    node.instructions.clear();
	    emit(new LabelInstruction(node.name));
	    emit(new NativeInstruction("JSR","AVM.EXECUTE.FN"));
	    if (node.frameSize > 0) {
		emit(new ISPInstruction(node.frameSize));
	    }

	    scan(node.varDecls);
	    scan(node.statements);

	    if (!node.endsInReturnStatement()) {
		emit(new RAFInstruction(0));
	    }
	}
    }

    public void visitVarDecl(VarDecl node) 
	throws ACCError
    {
	if (node.init != null) {
	    // Evaluate intializer expr
	    needAddress = false;
	    scan(node.init);
	    // Adjust the size
	    adjustSize(node.size,node.init.size,node.init.isSigned);
	    // Do the assignment.
	    emit(new PVAInstruction(node.getOffset()));
	    emit(new STMInstruction(node.size));
	}
    }

    public void visitIfStatement(IfStatement node) 
	throws ACCError
    {
	// Construct the labels
	boolean needTest = !node.test.isTrue() && !node.test.isFalse();
	String label = getLabel();
	LabelInstruction trueLabel = 
	    new LabelInstruction(mangle("TRUE"+label));
	LabelInstruction falseLabel = 
	    new LabelInstruction(mangle("FALSE"+label));
	LabelInstruction endLabel = 
	    new LabelInstruction(mangle("ENDIF"+label));
	// Test and branch
	if (needTest) {
	    needAddress = false;
	    scan(node.test);
	    emit(new BRFInstruction(falseLabel));
	}
	// True part
	if (!node.test.isFalse()) {
	    emit(trueLabel);
	    scan(node.thenPart);
	    if (node.elsePart != null && !node.test.isTrue()) {
		emit(new BRUInstruction(endLabel));
	    }
	}
	// False part
	if (!node.test.isTrue()) {
	    emit(falseLabel);
	    if (node.elsePart != null) {
		scan(node.elsePart);
		emit(endLabel);
	    }
	}
    }

    public void visitWhileStatement(WhileStatement node) 
	throws ACCError
    {
	// Construct the labels
	String label = getLabel();
	LabelInstruction testLabel = 
	    new LabelInstruction(mangle("TEST"+label));
	LabelInstruction bodyLabel = 
	    new LabelInstruction(mangle("BODY"+label));
	LabelInstruction exitLabel = 
	    new LabelInstruction(mangle("EXIT"+label));
	// Test and branch
	needAddress = false;
	boolean needTest = !node.test.isTrue() && !node.test.isFalse();
	boolean needBody = !node.test.isFalse();
	if (needTest) {
	    emit(testLabel);
	    scan(node.test);
	    emit(new BRFInstruction(exitLabel));
	}
	if (needBody) {
	    // Loop body
	    emit(bodyLabel);
	    scan(node.body);
	    if (needTest) {
		emit(new BRUInstruction(testLabel));
	    }
	    else {
		emit(new BRUInstruction(bodyLabel));
	    }
	}
	if (needTest) {
	    // Loop exit
	    emit(exitLabel);
	}
    }

    public void visitSetStatement(SetStatement node) 
	throws ACCError
    {
	// Evaluate RHS as value
	needAddress = false;
	scan(node.rhs);
	adjustSize(node.lhs.getSize(),node.rhs.getSize(),
		   node.rhs.isSigned);
	// Evaluate LHS as address.
	needAddress = true;
	scan(node.lhs);
	// Store RHS to (LHS)
	emit(new STMInstruction(node.lhs.getSize()));
    }

    public void visitCallStatement(CallStatement node) 
	throws ACCError
    {
	super.visitCallStatement(node);
	if (node.expr.size > 0) {
	    emit(new DSPInstruction(node.expr.size));
	}
    }

    public void visitIncrStatement(IncrStatement node)
	throws ACCError
    {
	needAddress = true;
	scan(node.expr);
	emit(new ICRInstruction(node.expr.size));
    }

    public void visitDecrStatement(DecrStatement node)
	throws ACCError
    {
	needAddress = true;
	scan(node.expr);
	emit(new DCRInstruction(node.expr.size));
    }

    public void visitReturnStatement(ReturnStatement node)
	throws ACCError 
    {
	if (node.expr != null) {
	    // Evaluate expression
	    needAddress = false;
	    scan(node.expr);
	    adjustSize(currentFunction.size,node.expr.size,
		       node.expr.isSigned);
	}
	emit(new RAFInstruction(currentFunction.size));
    }

    public void visitBlockStatement(BlockStatement node) 
	throws ACCError
    {
	// Nothing special to do
	super.visitBlockStatement(node);
    }

    public void visitIndexedExpression(IndexedExpression node)
	throws ACCError
    {
	// Record whether our result should be an address.
	boolean parentNeedsAddress = needAddress;
	// Evaluate indexed expr.
	needAddress = false;
	scan(node.indexed);
	// Pad to 2 bytes if necessary
	adjustSize(2,node.indexed.size,false);
	if (!node.index.isZero()) {
	    // Evaluate index expr.
	    needAddress = false;
	    scan(node.index);
	    // Pad to 2 bytes if necessary
	    adjustSize(2,node.index.size,node.index.isSigned);
	    // Pull LHS address and RHS index, add them, and put
	    // result on the stack.
	    emit(new ADDInstruction(2));
	}
	// If parent wanted a value, compute it now.
	if (!parentNeedsAddress) {
	    emit(new MTSInstruction(node.size));
	}
    }

    public void visitCallExpression(CallExpression node) 
	throws ACCError
    {
	boolean needIndirectCall = true;
	if (node.fn instanceof Identifier) {
	    Identifier id = (Identifier) node.fn;
	    Node def = id.def;
	    if (def instanceof FunctionDecl) {
		emitCallToFunctionDecl((FunctionDecl) def,
				       node.args);
		needIndirectCall = false;
	    }
	    else if (def instanceof ConstDecl ||
		     def instanceof DataDecl) {
		emitCallToConstant(id.name);
		needIndirectCall = false;
	    }
	}
	else if (node.fn instanceof NumericConstant) {
	    NumericConstant nc = (NumericConstant) node.fn;
	    emitCallToConstant(nc.valueAsHexString());
	    needIndirectCall = false;
	}
	// If all else failed, use indirect call
	if (needIndirectCall) {
	    emitIndirectCall(node.fn);
	}
    }

    /**
     * Call to declared function: push args, restore regs, call, and
     * save regs.
     */
    private void emitCallToFunctionDecl(FunctionDecl functionDecl,
					List<Expression> args) 
	throws ACCError
    {
	// Fill in the arguments, if any
	Iterator<VarDecl> I = functionDecl.params.iterator();
	if (args.size() > 0) {
	    // Save bump size for undo
	    int bumpSize = SAVED_INFO_SIZE;
	    // Save place for return addresses and saved FP
	    emit(new ISPInstruction(SAVED_INFO_SIZE));
	    for (Expression arg : args) {
		VarDecl param = I.next();
		// Evaluate the argument
		needAddress = false;
		scan(arg);
		// Adjust sizes to match.
		adjustSize(param.size,arg.size,arg.isSigned);
		bumpSize += param.size;
	    }
	    // Bump SP back down to new FP for function entry
	    emit(new DSPInstruction(bumpSize));
	}
	restoreRegisters();
	emit(new CFDInstruction(new Address(functionDecl.name)));
	saveRegisters();
    }

    /**
     * Calling a constant address: restore regs, call, and save regs.
     */
    private void emitCallToConstant(String addr) {
	restoreRegisters();
	emit(new CFDInstruction(new Address(addr)));
	saveRegisters();
    }

    /**
     * Indirect function call: evaluate expression, restore regs, call,
     * and save regs.
     */
    private void emitIndirectCall(Expression node) 
	throws ACCError
    {
	needAddress = false;
	scan(node);
	adjustSize(2,node.size,node.isSigned);
	restoreRegisters();
	emit(new CFIInstruction());
	saveRegisters();
    }

    public void visitRegisterExpression(RegisterExpression node) 
	throws ACCError
    {
	emit(new PVAInstruction(node.register.getOffset()));
	if (!needAddress) {
	    emit(new MTSInstruction(1));
	}
    }

    public void visitBinopExpression(BinopExpression node) 
	throws ACCError
    {
	int size = Math.max(node.left.size,node.right.size);
 	// Evaluate left
	needAddress = false;
	scan(node.left);
	adjustSize(size,node.left.size,node.left.isSigned);
 	// Evaluate right
	needAddress = false;
	scan(node.right);
	if (node.operator.compareTo(BinopExpression.Operator.SHR) > 0) {
	    adjustSize(size,node.right.size,node.right.isSigned);
	}
	// Do the operation
	boolean signed =
	    node.left.isSigned || node.right.isSigned;
	switch (node.operator) {
	case SHL:
	    emit(new SHLInstruction(size));
	    break;
	case SHR:
	    emit(new SHRInstruction(size, signed));
	    break;
	case TIMES:
	    emit(new MULInstruction(size, signed));
	    break;
	case DIVIDE:
	    emit(new DIVInstruction(size, signed));
	    break;
	case PLUS:
	    emit(new ADDInstruction(size));
	    break;
	case MINUS:
	    emit(new SUBInstruction(size));
	    break;
	case EQUALS:
	    emit(new TEQInstruction(size));
	    break;
	case GT:
	    emit(new TGTInstruction(size, signed));
	    break;
	case GEQ:
	    emit(new TGEInstruction(size, signed));
	    break;
	case LT:
	    emit(new TLTInstruction(size, signed));
	    break;
	case LEQ:
	    emit(new TLEInstruction(size, signed));
	    break;
	case AND:
	    emit(new ANLInstruction(size));
	    break;
	case OR:
	    emit(new ORLInstruction(size));
	    break;
	case XOR:
	    emit(new ORXInstruction(size));
	    break;
	}
    }

    public void visitUnopExpression(UnopExpression node) 
	throws ACCError
    {
	switch(node.operator) {
	case DEREF:
	    needAddress = true;
	    scan(node.expr);
	    break;
	case NOT:
	    needAddress = false;
	    scan(node.expr);
	    emit(new NOTInstruction(node.expr.size));
	    break;
	case NEG:
	    needAddress = false;
	    scan(node.expr);
	    emit(new NEGInstruction(node.expr.size));
	    break;
	default:
	    throw new ACCInternalError("unhandled unary operator",node);
	}
    }

    public void visitParensExpression(ParensExpression node) 
	throws ACCError
    {
	// Nothing special to do
	super.visitParensExpression(node);
    }

    /* Helper methods */

    private String mangle(String s) {
	return currentFunction.name+"."+s;
    }

    /**
     * Print out debugging info
     */
    public void printStatus(String s) {
	if (debug) {
	    System.err.println(s);
	}
    }

    /**
     * Get a fresh branch label
     */
    private String getLabel() {
	return "." + branchLabelCount++;
    }

    /**
     * Record the local variables in the current frame and compute
     * their stack slots.
     */
    private void computeStackSlotsFor(FunctionDecl node) 
	throws ACCError
    {
	int offset = 0;
	printStatus("stack slots:");
	// Params
	for (VarDecl varDecl : node.params) {
	    printStatus(" " + varDecl + ",offset=" + offset);
	    varDecl.setOffset(offset);
	    offset += varDecl.getSize();
	}
	int firstLocalVarOffset = offset;
	// Local vars
	for (VarDecl varDecl : node.varDecls) {
	    printStatus(" " + varDecl + ",offset=" + offset);
	    varDecl.setOffset(offset);
	    offset += varDecl.getSize();
	}
	// Saved regs
	savedRegs.runOn(node);
	for (RegisterExpression.Register reg : node.savedRegs) {
	    printStatus(" " + reg + ",offset=" + offset);
	    reg.setOffset(offset);
	    offset += reg.getSize();
	}

	// Compute and store the frame size.
	node.frameSize = offset;
	printStatus("frame size="+node.frameSize);
    }

    /**
     * If a register expression for register R appears in the function
     * body, then we need a stack slot for R.  Find all those
     * registers now.
     */
    private SavedRegs savedRegs = new SavedRegs();
    private class SavedRegs 
	extends ASTScanner
	implements FunctionPass 
    {
	FunctionDecl fn;
	public void runOn(FunctionDecl node)
	    throws ACCError
	{
	    fn = node;
	    scan(node);
	}

	public void visitRegisterExpression(RegisterExpression node) {
	    fn.savedRegs.add(node.register);
	}
    }

    /**
     * Make a value on the stack bigger or smaller if necessary to fit
     * needed size.
     */
    private void adjustSize(int targetSize, int stackSize,
			    boolean signed) {
	if (targetSize < stackSize) {
	    emit(new DSPInstruction(stackSize-targetSize));
	}
	if (targetSize > stackSize) {
	    emit(new EXTInstruction(targetSize-stackSize, signed));
	}
    }

    /**
     * Emit code to restore all registers to the values saved in their
     * spill slots on the program stack.
     */
    private void restoreRegisters() {
	if (currentFunction.savedRegs.size() > 0) {
	    for (RegisterExpression.Register reg : 
		     currentFunction.savedRegs) {
		emit(new VTMInstruction(reg.getOffset(),
					new Address(reg.saveAddr)));
	    }
	}
    }

    /**
     * Emit code to save the registers to their spill slots.
     */
    private void saveRegisters() {
	if (currentFunction.savedRegs.size() > 0) {
	    for (RegisterExpression.Register reg :
		     currentFunction.savedRegs) {
		emit(new MTVInstruction(reg.getOffset(),
					new Address(reg.saveAddr)));
	    }
	}
    }

    private void emit(Instruction inst) {
	currentFunction.instructions.add(inst);
    }

}
