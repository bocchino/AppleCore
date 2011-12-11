/**
 * Scan the AST and evaluate constant-value expressions.
 */
package AppleCoreCompiler.Transforms;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

import java.math.BigInteger;
import java.util.*;

public class ConstantEvaluationPass
    extends ExpressionTransformer
    implements Pass
{
    
    /**
     * Process the AST
     */
    public void runOn(SourceFile sourceFile) 
	throws ACCError
    {
	scan(sourceFile);
    }

    public void visitIdentifier(Identifier node) 
	throws ACCError
    {
	super.visitIdentifier(node);
	if (node.def instanceof ConstDecl) {
	    ConstDecl cd = (ConstDecl) node.def;
	    result = cd.expr;
	}
    }

    public void visitBinopExpression(BinopExpression node) 
	throws ACCError
    {
	super.visitBinopExpression(node);
	if (node.left instanceof NumericConstant &&
	    node.right instanceof NumericConstant) {
	    BigInteger leftValue = 
		((NumericConstant) node.left).valueAsBigInteger();
	    BigInteger rightValue =
		((NumericConstant) node.right).valueAsBigInteger();
	    IntegerConstant ic  = new IntegerConstant();
	    ic.lineNumber = node.lineNumber;
	    switch (node.operator) {
	    case SHL:
		ic.value = 
		    leftValue.shiftLeft(rightValue.intValue());
		break;
	    case SHR:
		ic.value =
		    leftValue.shiftRight(rightValue.intValue());
		break;
	    case TIMES:
		ic.value =
		    leftValue.multiply(rightValue);
		break;
	    case DIVIDE:
		ic.value = 
		    leftValue.divide(rightValue);
		break;
	    case PLUS:
		ic.value = 
		    leftValue.add(rightValue);
		break;
	    case MINUS:
		ic.value = 
		    leftValue.subtract(rightValue);
		break;
	    case EQUALS:
		ic.value = (leftValue.equals(rightValue)) ?
		    BigInteger.ONE : BigInteger.ZERO;
		break;
	    case GT:
		ic.value = (leftValue.compareTo(rightValue) > 0)
		    ? BigInteger.ONE : BigInteger.ZERO;
		break;
	    case LT:
		ic.value = (leftValue.compareTo(rightValue) < 0)
		    ? BigInteger.ONE : BigInteger.ZERO;
		break;
	    case GEQ:
		ic.value = (leftValue.compareTo(rightValue) >= 0)
		    ? BigInteger.ONE : BigInteger.ZERO;
		break;
	    case LEQ:
		ic.value = (leftValue.compareTo(rightValue) <= 0)
		    ? BigInteger.ONE : BigInteger.ZERO;
		break;
	    case AND:
		ic.value = leftValue.and(rightValue);
		break;
	    case OR:
		ic.value = leftValue.or(rightValue);
		break;
	    case XOR:
		ic.value = leftValue.xor(rightValue);
		break;
	    }
	    result = ic;
	}
    }

    public void visitUnopExpression(UnopExpression node) 
	throws ACCError
    {
	super.visitUnopExpression(node);
	if (node.expr instanceof NumericConstant) {
	    BigInteger exprVal = 
		((NumericConstant) node.expr).valueAsBigInteger();
	    IntegerConstant ic  = new IntegerConstant();
	    ic.lineNumber = node.lineNumber;
	    switch (node.operator) {
	    case NEG:
		ic.value = exprVal.negate(); 
		result = ic;
		break;
	    case NOT:
		ic.value = exprVal.not();
		result = ic;
		break;
	    }
	}
    }

    public void visitParensExpression(ParensExpression node) 
	throws ACCError
    {
	super.visitParensExpression(node);
	if (node.expr instanceof NumericConstant)
	    result = node.expr;
    }

}