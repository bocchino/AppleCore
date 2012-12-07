/**
 * A top-down scan of the tree.
 */
package AppleCoreCompiler.AST;

import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import java.util.*;

public class ASTScanner extends NodeVisitor {

    public void scan(Node node) 
	throws ACCError 
    {
	if (node != null) node.accept(this);
    }

    public void scan(List<? extends Node> nodes) 
	throws ACCError 
    {
	if (nodes != null) {
	    for (Node node : nodes) {
		scan(node);
	    }
	}
    }

    /**
     * Override this method when you want to do something to every
     * node before visiting the children.
     */
    public void visitBeforeScan(Node node) throws ACCError {}

    /**
     * Override this method when you want to do something to every
     * node after visiting the children.
     */
    public void visitAfterScan(Node node) throws ACCError {}

    public void visitSourceFile(SourceFile node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.decls);
	visitAfterScan(node);
    }

    public void visitIncludeDecl(IncludeDecl node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	visitAfterScan(node);
    }

    public void visitConstDecl(ConstDecl node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.expr);
	visitAfterScan(node);
    }

    public void visitDataDecl(DataDecl node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.expr);
	scan(node.stringConstant);
	visitAfterScan(node);
    }
    public void visitVarDecl(VarDecl node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.init);
	visitAfterScan(node);
    }
    public void visitFunctionDecl(FunctionDecl node) 
	throws ACCError
    {
	visitBeforeScan(node);
	scan(node.params);
	scan(node.varDecls);
	scan(node.statements);
	visitAfterScan(node);
    }
    public void visitIfStatement(IfStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.test);
	scan(node.thenPart);
	scan(node.elsePart);
	visitAfterScan(node);
    }
    public void visitWhileStatement(WhileStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.test);
	scan(node.body);
	visitAfterScan(node);
    }
    public void visitSetStatement(SetStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.lhs);
	scan(node.rhs);
	visitAfterScan(node);
    }
    public void visitIncrStatement(IncrStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.expr);
	visitAfterScan(node);
    }
    public void visitDecrStatement(DecrStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.expr);
	visitAfterScan(node);
    }
    public void visitCallStatement(CallStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.expr);
	visitAfterScan(node);
    }
    public void visitReturnStatement(ReturnStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.expr);
	visitAfterScan(node);
    }
    public void visitBlockStatement(BlockStatement node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.statements);
	visitAfterScan(node);
    }
    public void visitIndexedExpression(IndexedExpression node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.indexed);
	scan(node.index);
	visitAfterScan(node);
    }
    public void visitCallExpression(CallExpression node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.fn);
	scan(node.args);
	visitAfterScan(node);
    }
    public void visitBinopExpression(BinopExpression node)
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.left);
	scan(node.right);
	visitAfterScan(node);
    }
    public void visitUnopExpression(UnopExpression node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.expr);
	visitAfterScan(node);
    }
    public void visitParensExpression(ParensExpression node) 
	throws ACCError 
    {
	visitBeforeScan(node);
	scan(node.expr);
	visitAfterScan(node);
    }
    public void visitSizedExpression(SizedExpression node)
	throws ACCError
    {
	visitBeforeScan(node);
	scan(node.expr);
	visitAfterScan(node);
    }
    public void visitNode(Node node) 
	throws ACCError
    {
	visitBeforeScan(node);
	visitAfterScan(node);
    }
}