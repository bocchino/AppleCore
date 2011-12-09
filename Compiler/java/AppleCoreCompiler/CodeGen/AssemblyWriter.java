/**
 * Generic pass for traversing the AST and writing out 6502 assembly
 * code.  This pass factors out assembler-independent syntax details.
 * To write code for a specific assembler, implement this pass and
 * provide the assembler-specific code emitter functions.
 *
 * Code generation uses the Apple II stack (the "machine stack") only
 * for JSR, RTS, and temporary saves via PHA and PHP.  It uses a
 * separately managed stack (the "program stack") with a 16-byte
 * pointer for everything else.  That way, running our programs won't
 * blow out the stack.  We do require that the total size of all local
 * vars (plus 2 bytes for saving the frame pointer) be <= 256 bytes;
 * that lets us use one-byte indexing into local vars from the frame
 * pointer.  However, dynamic frame sizes of > 256 bytes are possible,
 * by allocating memory on the stack above the local variable slots.
 *
 * Code generation uses the following registers in zero-page memory:
 *
 * - Stack pointer, SP (2 bytes): Points to first free byte after
 *   stack top
 * - Frame pointer FP (2 bytes): Points to start of current frame in
 *   program stack.
 * - Index pointer IP (2 bytes): Points to memory location being
 *   indexed.
 *
 * The code generator depends on support code provided elsewhere.  The
 * support code uses a few additional zero-page registers.
 */
package AppleCoreCompiler.CodeGen;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.AST.Node.RegisterExpression.Register;

import java.io.*;
import java.util.*;
import java.math.*;

public abstract class AssemblyWriter
    extends ASTScanner 
    implements Pass
{

    /* Initialization stuff */
    protected final PrintStream printStream;
    protected final String inputFileName;
    public AssemblyWriter(PrintStream printStream,
			  String inputFileName) {
	this.printStream = printStream;
	this.inputFileName = inputFileName;
    }

    public void runOn(Program program) 
	throws ACCError
    {
	scan(program);
    }

    /* State variables for tree traversal */

    /**
     * Comment indent
     */
    protected int commentIndent = 0;
    protected void indentComment() { commentIndent++; }
    protected void unindentComment() { commentIndent--; }

    /**
     * The function being processed
     */
    protected FunctionDecl currentFunction;

    /**
     * Counter for branch labels
     */
    protected int branchLabelCount = 1;

    /**
     * Whether a variable seen is being evaluated for its address or
     * its value.
     */
    protected boolean needAddress;

    /**
     * Are we in debug mode?
     */
    public boolean debug = false;

    /* Visitor methods */

    /* Leaf nodes */

    public void visitIntegerConstant(IntegerConstant node) {
	emitComment(node);
	IntegerConstant intConst = (IntegerConstant) node;
	int size = intConst.getSize();
	for (int i = 0; i < size; ++i) {
	    emitImmediateInstruction("LDA",intConst.valueAtIndex(i));
	    emitAbsoluteInstruction("JSR","ACC.PUSH.A");
	}
    }

    public void visitCharConstant(CharConstant node) {
	emitComment(node);
	CharConstant charConst = (CharConstant) node;
	emitImmediateInstruction("LDA","'"+charConst.value+"'",false);
	emitAbsoluteInstruction("JSR","ACC.PUSH.A");	    
    }

    public void visitIdentifier(Identifier node) 
	throws ACCError 
    {
	Node def = node.def;
	if (def instanceof VarDecl) {
	    VarDecl varDecl = (VarDecl) def;
	    if (needAddress) {
		// Push variable's address on stack
		pushVarAddr(varDecl);
	    }
	    else {
		if (varDecl.isLocalVariable && varDecl.size == 1) {
		    emitComment("value of " + node);
		    // Fast case:  reading a local variable
		    emitImmediateInstruction("LDY",varDecl.getOffset());
		    emitIndirectYInstruction("LDA","ACC.FP");
		    emitAbsoluteInstruction("JSR","ACC.PUSH.A");
		}
		else {
		    // Push variable's address on stack
		    pushVarAddr(varDecl);
		    // Use the address to get the value.
		    emitComment("value at address");
		    emitImmediateInstruction("LDA",node.size);
		    emitAbsoluteInstruction("JSR","ACC.EVAL");
		}
	    }
	}
	else if (def instanceof ConstDecl) {
	    emitComment(node);
	    ConstDecl cd = (ConstDecl) def;
	    scan(cd.expr);
	}
	else if (def instanceof DataDecl) {
	    emitComment(node);
	    DataDecl dataDecl = (DataDecl) def;
	    emitImmediateInstruction("LDA",labelAsString(dataDecl.label),false);
	    emitAbsoluteInstruction("JSR","ACC.PUSH.A");
	    emitImmediateInstruction("LDA",labelAsString(dataDecl.label),true);
	    emitAbsoluteInstruction("JSR","ACC.PUSH.A");
	}
    }

    /* Non-leaf nodes */

    public void visitProgram(Program node) 
	throws ACCError
    {
	emitSeparatorComment();
	emitComment("Assembly file generated by");
	emitComment("the AppleCore Compiler, v1.0");
	emitSeparatorComment();
	emitComment("START OF FILE " + inputFileName);
	emitSeparatorComment();
	FunctionDecl  firstFunction = null;
	for (Declaration decl : node.decls) {
	    if (decl instanceof FunctionDecl) {
		FunctionDecl functionDecl = (FunctionDecl) decl;
		if (firstFunction == null && !functionDecl.isExternal) {
		    firstFunction = (FunctionDecl) decl;
		}
	    }
	}
	if (firstFunction != null) {
	    emitComment("jump to first function");
	    emitAbsoluteInstruction("JMP",labelAsString(firstFunction.name));
	}
	super.visitProgram(node);
	emitSeparatorComment();
	emitComment("END OF FILE " + inputFileName);
	emitSeparatorComment();
    }

    public void visitFunctionDecl(FunctionDecl node) 
	throws ACCError
    {
	if (!node.isExternal) {
	    emitSeparatorComment();
	    emitComment("function " + node.name);
	    emitSeparatorComment();

	    printStatus("entering " + node);
	    
	    currentFunction = node;

	    computeStackSlotsFor(node);
	    
	    emitLabel(node.name);
	    emitLine();
	    
	    emitComment("bump stack to top of frame");
	    emitImmediateInstruction("LDA",node.frameSize);
	    emitAbsoluteInstruction("JSR","ACC.SP.UP.A");
	    
	    scan(node.varDecls);
	    scan(node.statements);

	    if (!node.endsInReturnStatement()) {
		emitComment("restore old frame and return");
		emitAbsoluteInstruction("JSR","ACC.SET.SP.TO.FP");
		emitAbsoluteInstruction("JMP","ACC.RESTORE.CALLER.FP");
	    }
	}
    }

    public void visitIncludeDecl(IncludeDecl node) {
	emitSeparatorComment();
	emitAbsoluteInstruction(".IN",node.filename);
    }

    public void visitDataDecl(DataDecl node) 
	throws ACCError
    {
	if (node.label != null)
	    emitLabel(node.label);
	if (node.stringConstant != null) {
	    emitStringConstant(node.stringConstant);
	    if (node.isTerminatedString) {
		emitStringTerminator();
	    }
	}
	else {
	    // Previous passes must ensure cast will succeed.
	    if (!(node.expr instanceof NumericConstant)) {
		throw new ACCInternalError("non-constant data for ", node);
	    }
	    NumericConstant nc = 
		(NumericConstant) node.expr;
	    emitAsData(nc);
	}
    }

    public void visitConstDecl(ConstDecl node) {
	Expression expr = node.expr;
	if (expr instanceof IntegerConstant) {
	    IntegerConstant ic = (IntegerConstant) expr;
	    if (ic.getSize() <= 2) {
		emitLabel(node.label);
		emitAbsoluteInstruction(".EQ", ic.valueAsHexString());
	    }
	}
	else {
	    CharConstant cc = (CharConstant) expr;
	    emitLabel(node.label);
	    emitAbsoluteInstruction(".EQ",cc.toString());
	}
    }
    
    public void visitVarDecl(VarDecl node) 
	throws ACCError
    {
	if (node.isLocalVariable) {
	    if (node.init != null) {
		// We have an initializer expression
		// Evaluate intializer expr
		needAddress = false;
		scan(node.init);
		// Adjust the size
		adjustSize(node.size,node.init.size,node.init.isSigned);
		// Do the assignment.
		emitComment("initialize " + node);
		emitImmediateInstruction("LDA",node.getOffset());
		emitAbsoluteInstruction("JSR","ACC.PUSH.SLOT");
		assign(node.size);
	    }
	}
	else {
	    emitLabel(node.name);
	    if (node.init == null) {
		emitBlockStorage(node.size);
	    }
	    else {
		// Previous passes must ensure cast will succeed.
		if (!(node.init instanceof NumericConstant)) {
		    throw new ACCInternalError("non-constant initializer expr for ", node);
		}
		NumericConstant constant = 
		    (NumericConstant) node.init;
		emitAsData(constant, node.size);
	    }
	}
    }

    public void visitIfStatement(IfStatement node) 
	throws ACCError
    {
	// Evaluate the test condition
	needAddress = false;
	scan(node.test);
	// Do the branch
	emitComment("if condition test");
	String label = getLabel();
	emitImmediateInstruction("LDA",node.test.size);
	emitAbsoluteInstruction("JSR","ACC.BOOLEAN");
	emitAbsoluteInstruction("BNE",labelAsString("true" + label));
	emitAbsoluteInstruction("JMP",labelAsString("false" + label));
	// True part
	emitLabel("true" + label);
	emit("\n");
	scan(node.thenPart);
	if (node.elsePart != null)
	    emitAbsoluteInstruction("JMP",labelAsString("endif" + label));
	// False part
	emitLabel("false" + label);
	emit("\n");
	if (node.elsePart != null) {
	    scan(node.elsePart);
	    emitLabel("endif"+label);
	    emit("\n");
	}
    }

    public void visitWhileStatement(WhileStatement node) 
	throws ACCError
    {
	// Evaluate the test condition
	String label = getLabel();
	needAddress = false;
	emitLabel("test" + label);
	emit("\n");
	scan(node.test);
	emitComment("while loop test");
	// Do the branch
	emitImmediateInstruction("LDA",node.test.size);
	emitAbsoluteInstruction("JSR","ACC.BOOLEAN");
	emitAbsoluteInstruction("BNE",labelAsString("true" + label));
	emitAbsoluteInstruction("JMP",labelAsString("false" + label));
	// True part
	emitLabel("true" + label);
	emit("\n");
	scan(node.body);
	emitAbsoluteInstruction("JMP",labelAsString("TEST"+label));
	// False part
	emitLabel("false" + label);
	emit("\n");
    }
    
    public void visitExpressionStatement(ExpressionStatement node) 
	throws ACCError
    {
	// Nothing special to do 
	super.visitExpressionStatement(node);
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
	    // Assign result and restore frame
	    emitComment("assign result, restore frame, and exit");
	    emitImmediateInstruction("LDA",currentFunction.size);
	    emitAbsoluteInstruction("JSR","ACC.SP.DOWN.A");
	    emitAbsoluteInstruction("JSR","ACC.SET.IP.TO.SP");
	    emitAbsoluteInstruction("JSR","ACC.SET.SP.TO.FP");
	    emitAbsoluteInstruction("JSR","ACC.RESTORE.CALLER.FP");
	    emitAbsoluteInstruction("JMP","ACC.EVAL.1");
	}
	else {
	    emitComment("restore old frame and return");
	    emitAbsoluteInstruction("JSR","ACC.SET.SP.TO.FP");
	    emitAbsoluteInstruction("JMP","ACC.RESTORE.CALLER.FP");
	}
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
	// Evaluate index expr.
	needAddress = false;
	scan(node.index);
	// Pad to 2 bytes if necessary
	adjustSize(2,node.index.size,node.index.isSigned);
	// Pull LHS address and RHS index, add them, and put
	// result on the stack.
	emitComment("index");
	emitImmediateInstruction("LDA",2);
	emitAbsoluteInstruction("JSR","ACC.BINOP.ADD");
	// If parent wanted a value, compute it now.
	if (!parentNeedsAddress) {
	    emitComment("value at index");
	    emitImmediateInstruction("LDA",node.size);
	    emitAbsoluteInstruction("JSR","ACC.EVAL");
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
		emitCallToConstant(labelAsString(id.name));
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
     * Call to declared function: push args and set up new frame.
     */
    private void emitCallToFunctionDecl(FunctionDecl functionDecl,
					List<Expression> args) 
	throws ACCError
    {
	emitComment("fill slots for new frame");
	// Save bump size for undo
	int bumpSize = 2;
	// Push old FP
	emitAbsoluteInstruction("JSR","ACC.PUSH.FP");
	// Fill in the arguments
	Iterator<VarDecl> I = functionDecl.params.iterator();
	if (args.size() > 0) {
	    for (Expression arg : args) {
		VarDecl param = I.next();
		emitComment("bind arg to " + param);
		// Evaluate the argument
		needAddress = false;
		scan(arg);
		// Adjust sizes to match.
		adjustSize(param.size,arg.size,arg.isSigned);
		bumpSize += param.size;
	    }
	}
	emitComment("set FP for new frame");
	// Bump SP back down to new FP
	emitImmediateInstruction("LDA",bumpSize);
	emitAbsoluteInstruction("JSR","ACC.SP.DOWN.A");
	// Save new FP
	emitAbsoluteInstruction("JSR","ACC.SET.FP.TO.SP");
	emitComment("function call");
	emitAbsoluteInstruction("JSR",labelAsString(functionDecl.name));
    }
    
    /**
     * Calling a constant address: restore regs, JSR, and save regs.
     */
    private void emitCallToConstant(String addr) {
	restoreRegisters();
	emitComment("function call");
	emitAbsoluteInstruction("JSR",addr);
	saveRegisters();
    }

    /**
     * Indirect function call: evaluate expression, restore regs, JSR,
     * and save regs.
     */
    private void emitIndirectCall(Expression node) 
	throws ACCError
    {
	needAddress = false;
	scan(node);
	adjustSize(2,node.size,false);
	restoreRegisters();
	emitComment("function call");
	emitAbsoluteInstruction("JSR","ACC.INDIRECT.CALL");
	saveRegisters();
    }

    public void visitRegisterExpression(RegisterExpression node) 
	throws ACCError
    {
	if (needAddress) {
	    emitComment("address of slot for " + node.register);
	    emitImmediateInstruction("LDA",node.register.getOffset());
	    emitAbsoluteInstruction("JSR","ACC.PUSH.SLOT");
	} else {
	    emitComment("value in slot for " + node.register);
	    emitImmediateInstruction("LDY",node.register.getOffset());
	    emitIndirectYInstruction("LDA","ACC.FP");
	    emitAbsoluteInstruction("JSR","ACC.PUSH.A");
	}
    }

    public void visitSetExpression(SetExpression node) 
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
	assign(node.lhs.getSize());
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
	emitComment(node);
	if (node.hasSignedness()) {
	    // Load signedness
	    if (node.left.isSigned || node.right.isSigned) {
		emitComment("signed");
		emitImmediateInstruction("LDX",1);
	    }
	    else {
		emitComment("unsigned");
		emitImmediateInstruction("LDX",0);
	    }
	}
	// Load size
	emitImmediateInstruction("LDA",size);
	// Do the operation
	emitAbsoluteInstruction("JSR","ACC.BINOP."+
				node.operator.name);
    }

    public void visitUnopExpression(UnopExpression node) 
	throws ACCError
    {
	switch(node.operator) {
	case DEREF:
	    // Evaluate expr as address.
	    emitComment(node);
	    needAddress = true;
	    scan(node.expr);
	    break;
	case NOT:
	case NEG:
	    // Evaluate expr as value.
	    needAddress = false;
	    scan(node.expr);
	    emitComment(node);
	    // Do the operation.
	    emitImmediateInstruction("LDA",node.expr.size);
	    emitAbsoluteInstruction("JSR","ACC.UNOP."+node.operator.name);
	    break;
	case INCR:
	case DECR:
	    // Evaluate expr as address.
	    needAddress = true;
	    scan(node.expr);
	    emitComment(node);
	    // Do the operation.
	    emitImmediateInstruction("LDA",node.expr.size);
	    emitAbsoluteInstruction("JSR","ACC.UNOP."+node.operator.name);
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
    protected String getLabel() {
	return "." + branchLabelCount++;
    }

    /**
     * Record the local variables in the current frame and compute
     * their stack slots.
     */
    private void computeStackSlotsFor(FunctionDecl node) 
	throws ACCError
    {
	printStatus("stack slots:");
	int offset = 0;
	printStatus(" FP: " + offset);
	// Two bytes for saved FP
	offset += 2;
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
     * Push a size on the stack
     */
    protected void pushSize(int size) {
	emitComment("push expr size");
	emitImmediateInstruction("LDA",size);
	emitAbsoluteInstruction("JSR","ACC.PUSH.A");
    }

    /**
     * Make a value on the stack bigger or smaller if necessary to fit
     * needed size.
     */
    protected void adjustSize(int targetSize, int stackSize,
			      boolean signed) {
	if (targetSize < stackSize) {
	    emitImmediateInstruction("LDA",stackSize-targetSize);
	    emitAbsoluteInstruction("JSR","ACC.SP.DOWN.A");
	}
	if (targetSize > stackSize) {
	    if (signed) {
		emitComment("sign extend");
		emitImmediateInstruction("LDX",1);
	    }
	    else {
		emitComment("zero extend");
		emitImmediateInstruction("LDX",0);
	    }
	    emitImmediateInstruction("LDA",targetSize-stackSize);
	    emitAbsoluteInstruction("JSR","ACC.EXTEND");
	}
    }

    /**
     * Emit code to do an assignment through IP. Pull 'pullSize' bytes
     * off the stack and assign the low-order 'copySize' bytes through
     * IP.
     *
     * @param copySize  How many bytes to copy
     * @param pullSize  How many bytes to pull
     */
    protected void assign(int targetSize) {
	// Pull IP from stack, then copy from stack to (IP).
	emitComment("assign");
	//adjustSize(targetSize, stackSize, signed);
	emitImmediateInstruction("LDA",targetSize);
	emitAbsoluteInstruction("JSR","ACC.ASSIGN");
    }

    /**
     * Emit code to push the address of a variable on the stack.
     */
    protected void pushVarAddr(VarDecl node) {
	emitComment("address of " + node);
	if (node.isLocalVariable) {
	    emitImmediateInstruction("LDA",node.getOffset());
	    emitAbsoluteInstruction("JSR","ACC.PUSH.SLOT");
	}
	else {
	    emitImmediateInstruction("LDA",node.name,false);
	    emitAbsoluteInstruction("JSR","ACC.PUSH.A");
	    emitImmediateInstruction("LDA",node.name,true);
	    emitAbsoluteInstruction("JSR","ACC.PUSH.A");
	}
    }

    /**
     * Emit code to restore all registers to the values saved in their
     * spill slots on the program stack.
     */
    protected void restoreRegisters() {
	if (currentFunction.savedRegs.size() > 0) {
	    emitComment("restore registers");
	    for (RegisterExpression.Register reg : 
		     currentFunction.savedRegs) {
		emitImmediateInstruction("LDY",reg.getOffset());
		emitIndirectYInstruction("LDA","ACC.FP");
		emitAbsoluteInstruction("STA",reg.saveAddr);
	    }
	    emitAbsoluteInstruction("JSR","$FF3F");
	}
    }

    /**
     * Emit code to save the regsiters.
     */
    protected void saveRegisters() {
	if (currentFunction.savedRegs.size() > 0) {
	    emitComment("save registers");
	    emitAbsoluteInstruction("JSR","$FF4A");
	    for (RegisterExpression.Register reg :
		     currentFunction.savedRegs) {
		emitAbsoluteInstruction("LDA",reg.saveAddr);
		emitImmediateInstruction("LDY",reg.getOffset());
		emitIndirectYInstruction("STA","ACC.FP");
	    }
	}
    }


    /* Code emitter methods.  Override these to provide the syntax for
     * your favorite assembler. */

    protected void emitLine() {
	printStream.println();
    }

    protected void emit(String s) {
	printStream.print(s);
    }

    protected void emit(char ch) {
	printStream.print(ch);
    }

    protected abstract String addrString(int addr);
    protected abstract void emitInstruction(String s);
    protected abstract void emitAbsoluteInstruction(String mnemonic, int addr);
    protected abstract void emitAbsoluteInstruction(String mnemonic, 
						    String label);
    protected abstract void emitImmediateInstruction(String mnemonic, 
						     String imm, boolean high);
    protected abstract void emitImmediateInstruction(String mnemonic, int imm);
    protected void emitIndirectYInstruction(String mnemonic, int addr) {
	emitIndirectYInstruction(mnemonic, addrString(addr));
    }
    protected abstract void emitIndirectYInstruction(String mnemonic, String addr);
    protected abstract void emitIndirectXInstruction(String mnemonic, int addr);
    protected abstract void emitIndexedInstruction(String mnemonic, 
						   int addr, String reg);
    protected abstract void emitAbsoluteXInstruction(String mnemonic, String addr);
    protected abstract void emitComment(String comment);
    protected void emitComment(Object o) { emitComment(o.toString()); }
    protected abstract void emitSeparatorComment();
    protected void emitTODO(Object obj) {
	emitComment("--->TODO " + obj + "<---");
    }

    protected void emitLabel(String label) {
	emit(labelAsString(label));
    }

    protected abstract void emitAsData(NumericConstant c);
    protected abstract void emitStringTerminator();
    /**
     * Emit data with a size bound, for initializing constant
     * variables and data.
     */
    protected abstract void emitAsData(NumericConstant c, int sizeBound);
    protected abstract void emitBlockStorage(int nbytes);
    protected abstract void emitStringConstant(StringConstant sc);

    protected abstract String labelAsString(String label);

}