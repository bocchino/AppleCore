/**
 * Scan the AST and fill in the size and signedness of each expression
 * node, and whether each node represents an address.  Also do
 * semantic checking relating to size, signedness, and addresses.
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

import java.util.*;
import java.math.*;

public class SizePass 
    extends ASTScanner 
    implements Pass
{

    public boolean debug;

    public void printStatus(Expression node) {
	if (debug) {
	    System.out.print("line " + node.lineNumber + ": size of " 
			     + node + " is " + node.size + " " + 
			     (node.isSigned ? "signed" : "unsigned"));
	    if (node.representsAddress)
		System.out.print(", represents address");
	    System.out.println();
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
	    if (node.size > 0) {
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
	    frameSize += param.size;
	}
	for (VarDecl localVar : node.varDecls) {
	    frameSize += localVar.size;
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
	int exprSize = (node.expr == null) ? 0 : node.expr.size;
	if (currentFunction.size == 0 && exprSize > 0) {
	    throw new SemanticError("function has no return value",node);
	}
	if (exprSize == 0 && currentFunction.size > 0) {
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
	if (node.size == 0) {
	    throw new SemanticError(node.name + " has zero size", node);
	}
	if (node.init != null)
	    checkAssignment(node,node.init,node);
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
	if (node.size < 1 || node.size > 256) {
	    throw new SemanticError("index size " + node.size + 
				    " out of range", node);
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
		node.size = def.getSize();
		node.representsAddress = def.representsAddress();
		node.isSigned = def.isSigned();
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
	    node.size = 0;
	    node.representsAddress = true;
	    node.isSigned = false;
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
	if (!rhs.isConst()) {
	    if (rhs.representsAddress() && !lhs.representsAddress())
		throw new SemanticError("cannot assign address variable " + rhs +
					" to " + lhs,
					parent);
	}
    }

    /**
     * The size of a register expression is 1.  It is always unsigned.
     */
    public void visitRegisterExpression(RegisterExpression node) {
	node.size = 1;
	node.representsAddress = false;
	node.isSigned = false;
	printStatus(node);
    }

    /**
     * The size and signedness of an identifier come from its
     * definition.
     */
    public void visitIdentifier(Identifier node) {
	if (node.def instanceof FunctionDecl ||
	    node.def instanceof DataDecl) {
	    node.size=2;
	    node.representsAddress = true;
	    node.isSigned=false;
	}
	else {
	    node.size = node.def.getSize();
	    node.representsAddress = node.def.representsAddress();
	    node.isSigned = node.def.isSigned();
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
	    node.size = 1;
	    node.representsAddress = false;
	    node.isSigned = false;
	    break;
	case SHL: case SHR:
	    node.size = node.left.size;
	    node.representsAddress = node.left.representsAddress;
	    node.isSigned = node.left.isSigned;
	    checkShift(node);
	    break;
	default:
	    node.size = Math.max(node.left.size,node.right.size);
	    if (node.operator == BinopExpression.Operator.DIVIDE) {
		node.representsAddress = node.left.representsAddress &&
		    !node.right.representsAddress;
	    }
	    else {
		node.representsAddress = node.left.representsAddress ?
		    !node.right.representsAddress : node.right.representsAddress;
	    }
	    node.isSigned = (node.left.isSigned || 
			     node.right.isSigned);
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
	if (node.right.isSigned ||
	    node.right.representsAddress ||
	    node.right.size != 1) {
	    throw new SemanticError("shift amount must be 1 byte unsigned",
				    node);
	}
    }
    
    /**
     * The size and signedness of a unop expression come from the
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
	    node.size = 2;
	    node.representsAddress = true;
	    node.isSigned = false;
	    break;
	case NEG:
	    node.size = node.expr.size;
	    node.representsAddress = false;
	    node.isSigned = true;
	    break;
	default:
	    node.size = node.expr.size;
	    node.representsAddress = false;
	    node.isSigned = node.expr.isSigned;
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
	node.size = node.expr.size;
	node.representsAddress = node.expr.representsAddress();
	node.isSigned = node.expr.isSigned;
    }

    public void visitCharConstant(CharConstant node) {
	node.size = 1;
	node.representsAddress = false;
	node.isSigned = false;
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
