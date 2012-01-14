/**
 * Scan the tree top-down and transform expressions.
 */
package AppleCoreCompiler.AST;

import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import java.util.*;

public class ExpressionTransformer 
    extends ASTScanner 
{

    /**
     * Variable to return the result of visiting an expression
     */
    protected Expression result;

    /**
     * Transform an expression
     */
    protected Expression transform(Expression expr) 
	throws ACCError
    {
	if (expr == null) return null;
	scan(expr);
	return result;
    }

    /**
     * Transform a list of expressions
     */
    protected List<Expression> transform(List<Expression> exprs) 
	throws ACCError
    {
	List<Expression> newExprs = 
	    new LinkedList<Expression>();
	for (Expression expr : exprs) {
	    newExprs.add(transform(expr));
	}
	return newExprs;
    }

    public void visitConstDecl(ConstDecl node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.expr = transform(node.expr);
	visitAfterScan(node);
    }

    public void visitDataDecl(DataDecl node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.expr = transform(node.expr);
	scan(node.stringConstant);
	visitAfterScan(node);
    }
    public void visitVarDecl(VarDecl node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.init = transform(node.init);
	visitAfterScan(node);
    }
    public void visitIfStatement(IfStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.test = transform(node.test);
	scan(node.thenPart);
	scan(node.elsePart);
	visitAfterScan(node);
    }
    public void visitWhileStatement(WhileStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.test = transform(node.test);
	scan(node.body);
	visitAfterScan(node);
    }
    public void visitSetStatement(SetStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.lhs = transform(node.lhs);
	node.rhs = transform(node.rhs);
	if (node.rhs == null) {
	    throw new ACCInternalError("null RHS", node);
	}
	visitAfterScan(node);
    }
    public void visitCallStatement(CallStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.expr = transform(node.expr);
	visitAfterScan(node);
    }
    public void visitIncrStatement(IncrStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.expr = transform(node.expr);
	visitAfterScan(node);
    }
    public void visitDecrStatement(DecrStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.expr = transform(node.expr);
	visitAfterScan(node);
    }
    public void visitReturnStatement(ReturnStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.expr = transform(node.expr);
	visitAfterScan(node);
    }
    public void visitIndexedExpression(IndexedExpression node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.indexed = transform(node.indexed);
	node.index   = transform(node.index);
	visitAfterScan(node);
	result = node;
    }
    public void visitCallExpression(CallExpression node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.fn = transform(node.fn);
	node.args = transform(node.args);
	visitAfterScan(node);
	result = node;
    }
    public void visitBinopExpression(BinopExpression node)
	throws ACCError 
    {
	visitBeforeScan(node);
	node.left = transform(node.left);
	node.right = transform(node.right);
	visitAfterScan(node);
	result = node;
    }
    public void visitUnopExpression(UnopExpression node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.expr = transform(node.expr);
	visitAfterScan(node);
	result = node;
    }
    public void visitParensExpression(ParensExpression node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	node.expr = transform(node.expr);
	visitAfterScan(node);
	result = node;
    }
    public void visitNode(Node node) 
	throws ACCError
    {
	super.visitNode(node);
	if (node instanceof Expression) {
	    result = (Expression) node;
	}
    }
    /*
    public void visitIdentifier(Identifier node) 
	throws ACCError
    {
	super.visitIdentifier(node);
	result = node;	
    }
    public void visitIntegerConstant(IntegerConstant node) 
	throws ACCError
    {
	super.visitIntegerConstant(node);
	result = node;
    }
    public void visitCharConstant(CharConstant node) 
	throws ACCError
    {
	super.visitCharConstant(node);
	result = node;
    }
    */

}