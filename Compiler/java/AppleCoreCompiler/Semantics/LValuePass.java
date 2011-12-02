/**
 * Do semantic checking related to lvalues:
 *
 * 1. The parser admits some syntactic "lvalues" (like constant
 *    labels) that aren't actual lvalues.  Only registers, variables,
 *    and indexed expressions are lvalues.
 *
 * 2. The LHS of a SET expression must be an lvalue.  
 *
 * 3. The operand of INCR, DECR must be an lvalue.
 * 
 * 4. The operand of @ must be an lvalue, and it must not be a
 *    register.
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

public class LValuePass 
    extends ASTScanner 
    implements Pass
{

    public boolean debug;

    public void runOn(Program program) 
	throws ACCError
    {
	scan(program);
    }

    public void visitSetExpression(SetExpression node) 
	throws ACCError
    {
	super.visitSetExpression(node);
	if (!isLValue(node.lhs)) {
	    throw new SemanticError("assignment to non-lvalue",
				    node);
	}
    }

    public void visitUnopExpression(UnopExpression node) 
	throws ACCError
    {
	super.visitUnopExpression(node);
	switch (node.operator) {
	case DEREF:
	    if (!isLValue(node.expr)) {
		throw new SemanticError("deref of non-lvalue",
					node);
	    }	    
	    if (node.expr instanceof RegisterExpression) {
		throw new SemanticError("deref of register",
					node);
	    }
	    break;
	case INCR:
	    if (!isLValue(node.expr)) {
		throw new SemanticError("incr of non-lvalue",
					node);
	    }
	    break;
	case DECR:
	    if (!isLValue(node.expr)) {
		throw new SemanticError("decr of non-lvalue",
					node);
	    }
	    break;
	}
    }

    private boolean isLValue(Node node) 
	throws ACCError
    {
	return lvalueChecker.isLValue(node);
    }
    private LValueChecker lvalueChecker = 
	new LValueChecker();
    private class LValueChecker extends NodeVisitor {

	private boolean result;
	
	public boolean isLValue(Node node) 
	    throws ACCError
	{
	    result = false;
	    node.accept(this);
	    return result;
	}
	
	public void visitIdentifier(Identifier node) {
	    result = node.def instanceof VarDecl;
	}
	
	public void visitIndexedExpression(IndexedExpression node) {
	    result = true;
	}

	public void visitRegisterExpression(RegisterExpression node) {
	    result = true;
	}
    }
}