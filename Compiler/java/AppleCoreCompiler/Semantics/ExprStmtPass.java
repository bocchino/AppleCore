/**
 * Check that an expression statement is a SET, CALL, INCR, or DECR.
 * This simple check traps the nasty error of writing X=2;
 * (comparison) when you meant SET X=2; (assignment).
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

public class ExprStmtPass
    extends ASTScanner 
    implements Pass
{

    public boolean debug;

    public void runOn(SourceFile sourceFile)
	throws ACCError
    {
	scan(sourceFile);
    }

    public void visitExpressionStatement(ExpressionStatement node) 
	throws ACCError
    {
	super.visitExpressionStatement(node);
	if (!isValid(node.expr)) {
	    throw new SemanticError("statement has no effect", node);
	}
    }

    private boolean isValid(Expression node) 
	throws ACCError
    {
	return exprStmtChecker.check(node);
    }
    private ExprStmtChecker exprStmtChecker = 
	new ExprStmtChecker();
    private class ExprStmtChecker extends NodeVisitor {

	private boolean result;

	public boolean check(Expression node) 
	    throws ACCError
	{
	    result = false;
	    node.accept(this);
	    return result;
	}

	public void visitCallExpression(CallExpression node) 
	    throws ACCError
	{
	    result = true;
	}

	public void visitUnopExpression(UnopExpression node) {
	    switch (node.operator) {
	    case INCR: case DECR:
		result = true;
	    }
	}

	public void visitParensExpression(ParensExpression node) 
	    throws ACCError
	{
	    result = isValid(node.expr);
	}
    }
}