/**
 * Scan the AST and evaluate constant-value expressions.
 */
package AppleCoreCompiler.Transforms;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.Semantics.*;

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
	    if (cd.expr != null)
		result = cd.expr;
	}
    }

    public void visitIndexedExpression(IndexedExpression node) 
	throws ACCError
    {
	super.visitIndexedExpression(node);
	if (node.indexed instanceof NumericConstant &&
	    node.index instanceof NumericConstant) {
	    // Index and indexed both constants: do the addition now.
	    BinopExpression newIndexed = new BinopExpression();
	    newIndexed.left = node.indexed;
	    newIndexed.operator = 
		BinopExpression.Operator.PLUS;
	    newIndexed.right = node.index;
	    newIndexed.lineNumber = node.indexed.lineNumber;
	    IntegerConstant newIndex = new IntegerConstant();
	    newIndex.lineNumber = node.index.lineNumber;
	    newIndex.setValue(BigInteger.ZERO);
	    node.indexed = transform(newIndexed);
	    node.index = newIndex;
	    result = node;
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
		SizePass.checkShift(node);
		ic.setValue(leftValue.shiftLeft(rightValue.intValue()));
		break;
	    case SHR:
		SizePass.checkShift(node);
		ic.setValue(leftValue.shiftRight(rightValue.intValue()));
		break;
	    case TIMES:
		ic.setValue(leftValue.multiply(rightValue));
		break;
	    case DIVIDE:
		ic.setValue(leftValue.divide(rightValue));
		break;
	    case PLUS:
		ic.setValue(leftValue.add(rightValue));
		break;
	    case MINUS:
		ic.setValue(leftValue.subtract(rightValue));
		break;
	    case EQUALS:
		ic.setValue((leftValue.equals(rightValue)) ?
			    BigInteger.ONE : BigInteger.ZERO);
		break;
	    case GT:
		ic.setValue((leftValue.compareTo(rightValue) > 0)
			    ? BigInteger.ONE : BigInteger.ZERO);
		break;
	    case LT:
		ic.setValue((leftValue.compareTo(rightValue) < 0)
			    ? BigInteger.ONE : BigInteger.ZERO);
		break;
	    case GEQ:
		ic.setValue((leftValue.compareTo(rightValue) >= 0)
			    ? BigInteger.ONE : BigInteger.ZERO);
		break;
	    case LEQ:
		ic.setValue((leftValue.compareTo(rightValue) <= 0)
			    ? BigInteger.ONE : BigInteger.ZERO);
		break;
	    case AND:
		ic.setValue(leftValue.and(rightValue));
		break;
	    case OR:
		ic.setValue(leftValue.or(rightValue));
		break;
	    case XOR:
		ic.setValue(leftValue.xor(rightValue));
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
		ic.setValue(exprVal.negate()); 
		result = ic;
		break;
	    case NOT:
		ic.setValue(exprVal.not());
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