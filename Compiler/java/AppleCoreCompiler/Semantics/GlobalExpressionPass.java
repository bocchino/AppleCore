/**
 * Scan the AST and enforce rules relating to expressions at global
 * scope (AppleCore Specification, s. 4.4)
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Syntax.*;
import AppleCoreCompiler.Errors.*;

import java.util.*;
import java.io.*;

public class GlobalExpressionPass 
    extends ASTScanner 
    implements Pass
{

    public void runOn(SourceFile sourceFile) 
	throws ACCError
    {
	scan(sourceFile);
    }

    public void visitConstDecl(ConstDecl node)
	throws ACCError
    {
	requireCompileConst(node.expr, node);
    }

    public void visitDataDecl(DataDecl node) 
	throws ACCError
    {
	requireAssembleConst(node.expr, node);
    }

    public void visitVarDecl(VarDecl node) 
	throws ACCError
    {
	requireCompileConst(node.type.sizeExpr,node);
	if (!node.isLocalVariable) {
	    requireAssembleConst(node.init,node);
	}
    }

    public void visitFunctionDecl(FunctionDecl node)
	throws ACCError
    {
	requireCompileConst(node.type.sizeExpr,node);
    }

    public void visitBeforeScan(Node node)
	throws ACCError
    {
	if (node instanceof Expression) {
	    Expression expr = (Expression) node;
	    requireCompileConst(expr.type.sizeExpr,expr);
	}
    }

    private void requireAssembleConst(Expression expr, Node node) 
	throws ACCError
    {
	if (expr != null && !expr.isAssembleConst()) {
	    throw new SemanticError("assembly-time constant value " +
				    "required", node);
	}
    }

    private void requireCompileConst(Expression expr, Node node) 
	throws ACCError
    {
	if (expr != null && !expr.isCompileConst()) {
	    throw new SemanticError("compile-time constant value " +
				    "required", node);
	}
    }

}