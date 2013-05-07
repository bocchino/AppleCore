/**
 * Scan the AST and resolve declared sizes.
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

import java.math.*;

public class DeclaredSizePass
    extends ASTScanner 
    implements Pass
{

    public static final BigInteger MAX_SIZE = 
	new BigInteger("255");

    public static final BigInteger MIN_SIZE = 
        BigInteger.ONE;


    public void runOn(SourceFile sourceFile) 
	throws ACCError
    {
	scan(sourceFile.importedDecls);
	scan(sourceFile);
    }

    public void visitVarDecl(VarDecl node)
	throws ACCError
    {
	if (node.type.sizeExpr != null) {
	    node.type.size = getSizeFrom(node.type.sizeExpr);	
	}
	if (!node.isExternal) {
	    super.visitVarDecl(node);
	}
    }

    public void visitDataDecl(DataDecl node) 
	throws ACCError
    {
	if (!node.isExternal) {
	    super.visitDataDecl(node);
	}
    }

    public void visitFunctionDecl(FunctionDecl node) 
	throws ACCError
    {
	if (node.type.sizeExpr != null) {
	    node.type.size = getSizeFrom(node.type.sizeExpr);	
	}
	scan(node.params);
	if (!node.isExternal) {
	    scan(node.varDecls);
	    scan(node.statements);
	}
    }

    public void visitBeforeScan(Node node)
	throws ACCError
    {
	if (node instanceof Expression) {
	    Expression expr = (Expression) node;
	    expr.createType();
	    if (expr.type.sizeExpr != null) {
		expr.type.size = getSizeFrom(expr.type.sizeExpr);	
	    }
	}
    }

    public void visitTypedExpression(TypedExpression node)
	throws ACCError
    {
	super.visitTypedExpression(node);
	if (node.type.sizeExpr != null) {
	    node.type.size = getSizeFrom(node.type.sizeExpr);	
	}
    }

    private static int getSizeFrom(Expression expr) 
	throws ACCError
    {
	if (expr instanceof IntegerConstant) {
	    IntegerConstant ic = (IntegerConstant) expr;
	    BigInteger bigInt = ic.valueAsBigInteger();
	    checkSizeBound(bigInt, expr);
	    return bigInt.intValue();
	}
	throw new ACCInternalError("non-constant size " + expr, expr);
    }

    private static void checkSizeBound(BigInteger bigInt,
				       Expression expr)
	throws ACCError
    {
	if (bigInt.compareTo(MIN_SIZE) < 0 ||
	    bigInt.compareTo(MAX_SIZE) > 0) {
	    throw new SemanticError("size " + expr +
				    " out of range", expr);
	}
    }


}
