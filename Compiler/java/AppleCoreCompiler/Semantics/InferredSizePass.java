/**
 * Scan the AST and infer size and signedness for each expression node
 * that does not explicitly declare them.  Also infer whether each
 * node represents an address.  Also do semantic checking relating to
 * size, signedness, and addresses.
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

import java.util.*;
import java.math.*;

public class InferredSizePass 
    extends ASTScanner 
    implements Pass
{

    public boolean debug;

    public void printStatus(Expression node) {
	if (debug) {
	    System.err.print("line " + node.lineNumber + ": size of " 
			     + node + " is " + node.getSize() + " " + 
			     (node.isSigned() ? "signed" : "unsigned"));
	    if (node.representsAddress())
		System.err.print(", represents address");
	    System.err.println();
	}
    }
    
    public void runOn(SourceFile sourceFile) 
	throws ACCError
    {
	scan(sourceFile);
    }

    FunctionDecl currentFunction;

    public void visitFunctionDecl(FunctionDecl node) 
	throws ACCError
    {
	currentFunction = node;
	super.visitFunctionDecl(node);
	if (!node.isExternal) {
	    if (node.getSize() > 0) {
		if (node.statements.size() == 0 || 
		    !(node.statements.get(node.statements.size()-1) 
		      instanceof ReturnStatement)) {
		    throw new SemanticError("function requires return value",
					    node);
		}
	    }
	}
	int frameSize = 0;
	for (VarDecl param : node.params) {
	    frameSize += param.type.size;
	}
	for (VarDecl localVar : node.varDecls) {
	    frameSize += localVar.type.size;
	}
	if (frameSize > 256)
	    throw new SemanticError("frame size of " + node + " is too large",
				    node);
    }

    /**
     * Return expression and function return size must both be zero,
     * or both not.
     */
    public void visitReturnStatement(ReturnStatement node) 
	throws ACCError
    {
	super.visitReturnStatement(node);
	int exprSize = (node.expr == null) ? 0 : node.expr.getSize();
	if (currentFunction.getSize() == 0 && exprSize > 0) {
	    throw new SemanticError("function has no return value",node);
	}
	if (exprSize == 0 && currentFunction.getSize() > 0) {
	    throw new SemanticError("function requires return value",node);
	}
	if (exprSize > 0) {
	    checkAssignment(currentFunction,node.expr,node);
	}
    }

    /**
     * A variable declaration must have size > 0.
     */
    public void visitVarDecl(VarDecl node) 
	throws ACCError
    {
	super.visitVarDecl(node);
	if (node.getSize() == 0) {
	    throw new SemanticError(node.name + " has zero size", node);
	}
	if (node.init != null) {
	    // See comment in visitDataDecl
	    if (!node.isLocalVariable && !node.init.isCompileConst()) {
		node.init.type.size = 2;
		node.init.type.isSigned = false;
	    }
	    checkAssignment(node,node.init,node);
	}
    }

    public void visitDataDecl(DataDecl node) 
	throws ACCError
    {
	super.visitDataDecl(node);
	// Non-constant expressions at global position are resolved by
	// the assembler, so the compiler gives them size 2 unsigned.
	// See AppleCore spec s.4.5
	if (node.expr != null && !node.expr.isCompileConst()) {
	    node.expr.type.size = 2;
	    node.expr.type.isSigned = false;
	}
    }

    /**
     * The size of an indexed expression comes from the expression
     * itself.  It is always unsigned.  The expression being indexed
     * must represent an address.
     */
    public void visitIndexedExpression(IndexedExpression node) 
	throws ACCError
    {
	super.visitIndexedExpression(node);
	requireAddress(node.indexed);
	if (node.index.getSize() == 0 ||
	    node.index.getSize() > 2) {
	    throw new SemanticError("index size must be 1 or 2",
				    node);
	}
	printStatus(node);
    }

    /**
     * If the call expression calls a defined function or function
     * prototype, then the size and signedness come from the
     * definition.  Otherwise, the size is 0 unsigned.
     */
    public void visitCallExpression(CallExpression node) 
	throws ACCError
    {
	super.visitCallExpression(node);
	boolean hasDecl = false;
	if (!node.fn.representsAddress()) {
	    throw new SemanticError("called expression must be address",
				    node);
	}
	if (node.fn instanceof Identifier) {
	    Identifier id = (Identifier) node.fn;
	    Node def = id.def;
	    if (def instanceof FunctionDecl) {
		hasDecl = true;
		node.type.size = def.getSize();
		node.type.representsAddress = def.representsAddress();
		node.type.isSigned = def.isSigned();
		// Check address assignment rules
		FunctionDecl functionDecl = (FunctionDecl) def;
		Iterator<VarDecl> I = functionDecl.params.iterator();
		for (Expression arg : node.args) {
		    VarDecl param = I.next();
		    checkAssignment(param,arg,node);
		}
	    }
	}
	if (!hasDecl) {
	    // We're calling a label with no prototype info
	    requireAddress(node.fn);
	    node.type.size = 0;
	    node.type.representsAddress = true;
	    node.type.isSigned = false;
	}
	printStatus(node);
    }

    /**
     * For a set statement, check validity of assignment to a variable
     * representing an address.
     */
    public void visitSetStatement(SetStatement node) 
	throws ACCError
    {
	super.visitSetStatement(node);
	checkAssignment(node.lhs,node.rhs,node);
    }


    private void checkAssignment(Node lhs, 
				 Expression rhs,
				 Node parent) 
	throws ACCError
    {
	if (lhs.representsAddress() && !rhs.representsAddress())
	    throw new SemanticError("cannot assign " + rhs +
				    " to address variable " + lhs,
				    parent);
    }

    /**
     * The size of a register expression is 1.  It is always unsigned.
     */
    public void visitRegisterExpression(RegisterExpression node) {
	node.type.size = 1;
	node.type.representsAddress = false;
	node.type.isSigned = false;
	printStatus(node);
    }

    /**
     * The size and signedness of an identifier come from its
     * definition.
     */
    public void visitIdentifier(Identifier node) {
	if (node.def instanceof FunctionDecl ||
	    node.def instanceof DataDecl) {
	    node.type.size=2;
	    node.type.representsAddress = true;
	    node.type.isSigned=false;
	}
	else {
	    node.type.size = node.def.getSize();
	    node.type.representsAddress = node.def.representsAddress();
	    node.type.isSigned = node.def.isSigned();
	}
	printStatus(node);
    }

    /**
     * The size and signedness of a binop expression is computed as
     * follows:
     *
     * 1. For comparison operations, the size is 1 byte unsigned.
     *
     * 2. For shifts, the size and signedness come from the left
     *    operand, and the right operand must be 1 byte unsigned.
     *
     * 3. For all other operations, the size is the maximum of the
     *    sizes of its operands.  If either of the operands is signed,
     *    the result is signed.  Otherwise, the result is unsigned.
     *
     * A binop expression represents an address if
     *
     * 1. It is a shift or divide operation, and the
     *    left-hand operand represents an address; or
     *
     * 2. It is not a shift, comparison, or divide operation, and
     *    either operand represents an address.
     */
    public void visitBinopExpression(BinopExpression node) 
	throws ACCError
    {
	super.visitBinopExpression(node);
	switch (node.operator) {
	case EQUALS: case GT: case LT: case GEQ: case LEQ:
	    node.type.size = 1;
	    node.type.representsAddress = false;
	    node.type.isSigned = false;
	    break;
	case SHL: case SHR:
	    node.type = node.left.type;
	    checkShift(node);
	    break;
	default:
	    node.type.size = 
		Math.max(node.left.getSize(),node.right.getSize());
	    if (node.operator == BinopExpression.Operator.DIVIDE) {
		node.type.representsAddress = 
		    node.left.representsAddress() &&
		    !node.right.representsAddress();
	    }
	    else {
		node.type.representsAddress = 
		    node.left.representsAddress() ||
		    node.right.representsAddress();
	    }
	    node.type.isSigned = (node.left.isSigned() || 
				  node.right.isSigned());
	    break;
	}
	printStatus(node);
    }

    /**
     * Check that the right-hand operator of a shift operation is one
     * byte unsigned.
     */
    public static void checkShift(BinopExpression node)
	throws ACCError
    {
	if (node.right.isSigned() ||
	    node.right.getSize() != 1) {
	    throw new SemanticError("shift amount must be 1 byte unsigned",
				    node);
	}
    }
    
    /**
     * The size and signedness of an unop expression come from the
     * operand except in the following cases:
     *
     * 1. An address operation @ is two bytes unsigned.
     *
     * 2. A negation operation is signed.
     */
    public void visitUnopExpression(UnopExpression node) 
	throws ACCError
    {
	super.visitUnopExpression(node);
	switch (node.operator) {
	case ADDRESS:
	    node.type.size = 2;
	    node.type.representsAddress = true;
	    node.type.isSigned = false;
	    break;
	case NEG:
	    node.type.size = node.expr.type.size;
	    node.type.representsAddress = false;
	    node.type.isSigned = true;
	    break;
	default:
	    node.type = node.expr.type;
	    node.type.representsAddress = false;
	}
    }

    /**
     * The size and signedness of a parens expression come from the
     * enclosed expression.
     */
    public void visitParensExpression(ParensExpression node) 
	throws ACCError
    {
	super.visitParensExpression(node);
	node.type = node.expr.type;
    }

    public void visitCharConstant(CharConstant node) {
	node.type.size = 1;
	node.type.representsAddress = false;
	node.type.isSigned = false;
    }

    /**
     * Require an address
     */
    private void requireAddress(Node node) 
	throws ACCError
    {
	if (!node.representsAddress()) {
	    throw new SemanticError(node + " must represent an address",
				    node);
	}
    }

}
