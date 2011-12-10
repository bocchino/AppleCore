/**
 * Class representing an abstract syntax tree node.
 */
package AppleCoreCompiler.AST;

import java.util.*;
import java.math.*;
import AppleCoreCompiler.Errors.*;

public abstract class Node {

    /**
     * The line number where the first token of this node appeared in
     * the source file.
     */
    public int lineNumber;
    
    /**
     * Generic visitor method, for traversing the AST.
     */    
    public abstract void accept(Visitor v) throws ACCError;

    /**
     * The size of the node, if it has one.
     */
    public int getSize() { return 0; }

    /**
     * The signedness of the node.
     */
    public boolean isSigned() { return false; }

    /**
     * Source files
     */
    public static class SourceFile extends Node {
	public String name;
	public boolean includeMode;
	public int origin;
	public final List<Declaration> decls = 
	    new LinkedList<Declaration>();

	public void accept(Visitor v) throws ACCError {
	    v.visitSourceFile(this);
	}

	public String toString() {
	    return "source file " + name;
	}
    }

    /**
     * Top-level program declarations
     */
    public static abstract class Declaration
	extends Node 
    {

    }

    public static class ConstDecl 
	extends Declaration 
    {
	public String label;
	public Expression expr;
	public int getSize() { return expr.getSize(); }
	public boolean isSigned() { return false; }

	public void accept(Visitor v) throws ACCError {
	    v.visitConstDecl(this);
	}

	public String toString() {
	    return "const decl " + label;
	}
    }

    public static class DataDecl 
	extends Declaration 
    {
	public String label;
	public Expression expr;
	public StringConstant stringConstant;
	public boolean isTerminatedString;

	public void accept(Visitor v) throws ACCError {
	    v.visitDataDecl(this);
	}

	/** Size is 2 because data decl is a pointer */
	public int getSize() { return 2; }

	public String toString() {
	    return "data decl " + label;
	}
    }

    /**
     * String constant literals
     */
    public static class StringConstant 
	extends Node
    {
	public String value;

	public boolean isZero() { return false; }

	public int getSize() {
	    return value.length();
	}

	public void accept(Visitor v) throws ACCError {
	    v.visitStringConstant(this);
	}

	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append("string constant \"");
	    sb.append(value);
	    sb.append("\"");
	    return sb.toString();
	}
    }

    public static class VarDecl 
	extends Declaration 
    {
	public String name;
	public int size;
	public boolean isSigned;
	public Expression init;

	public boolean isLocalVariable;
	public boolean isFunctionParam;
	public int offset;

	public void accept(Visitor v) throws ACCError {
	    v.visitVarDecl(this);
	}

	public int getSize() { return size; }
	public boolean isSigned() { return this.isSigned; }
	public int getOffset() { return offset; }
	public void setOffset(int offset) {
	    this.offset = offset;
	}
	
	public String toString() {
	    if (isFunctionParam)
		return "fn param " + name + ":" + 
		    sizeAsString(size,isSigned);
	    return "var decl " + name + ":" + 
		sizeAsString(size,isSigned);
	}
    }

    public static class FunctionDecl 
	extends Declaration 
    {
	public int size;
	public boolean isSigned;
	public String name;
	public final List<VarDecl> params = 
	    new LinkedList<VarDecl>();
	public final List<VarDecl> varDecls =
	    new LinkedList<VarDecl>();
	public Set<RegisterExpression.Register> savedRegs =
	    new TreeSet<RegisterExpression.Register>();
	public final List<Statement> statements =
	    new LinkedList<Statement>();

	public void accept(Visitor v) 
	    throws ACCError 
	{
	    v.visitFunctionDecl(this);
	}

	/**
	 * Size of the initial stack frame for this function.
	 */
	public int frameSize;

	/**
	 * Does this function have an external definition?
	 */
	public boolean isExternal;
	public int getSize() { return size; }
	public boolean isSigned() { return this.isSigned; }

	public boolean endsInReturnStatement() {
	    return (statements.size() > 0 && 
		    (statements.get(statements.size()-1) 
		     instanceof ReturnStatement));
	}

	public String toString() {
	    StringBuffer sb = 
		new StringBuffer("FN");
	    if (size > 0) {
		sb.append(':');
		sb.append(sizeAsString(size,isSigned));
	    }
	    sb.append(" ");
	    sb.append(name);
	    return sb.toString();
	}
    }

    public static class IncludeDecl
	extends Declaration
    {
	public String filename;

	public void accept(Visitor v) throws ACCError {
	    v.visitIncludeDecl(this);
	}

	public String toString() {
	    return "include decl " + filename;
	}
    }

    /**
     * Statements
     */
    public static abstract class Statement 
	extends Node 
    {

    }

    public static class IfStatement
	extends Statement
    {
	public Expression test;
	public Statement thenPart;
	public Statement elsePart;

	public void accept(Visitor v) throws ACCError {
	    v.visitIfStatement(this);
	}

	public String toString() {
	    return "if stmt";
	}
    }

    public static class WhileStatement
	extends Statement
    {
	public Expression test;
	public Statement body;

	public void accept(Visitor v) throws ACCError {
	    v.visitWhileStatement(this);
	}
       
	public String toString() {
	    return "while stmt";
	}
    }

    public static class ExpressionStatement
	extends Statement
    {
	public Expression expr;

	public void accept(Visitor v) throws ACCError {
	    v.visitExpressionStatement(this);
	}

	public String toString() {
	    return "expr stmt";
	}
    }

    public static class ReturnStatement
	extends Statement
    {
	public Expression expr;

	public void accept(Visitor v) throws ACCError {
	    v.visitReturnStatement(this);
	}

	public String toString() {
	    return "return stmt";
	}
    }

    public static class BlockStatement
	extends Statement
    {
	public final List<Statement> statements =
	    new LinkedList<Statement>();

	public void accept(Visitor v) throws ACCError {
	    v.visitBlockStatement(this);
	}

	public String toString() {
	    return "block stmt";
	}
    }

    /**
     * Expressions
     */
    public static abstract class Expression 
	extends Node 
    {
	/**
	 * The size of the value represented by this expression;
	 * filled in during semantic checking.
	 */
	public int size;

	public int getSize() { return size; }
	public boolean isSigned() { return this.isSigned; }

	/**
	 * Whether the value represented by this expression is signed.
	 */
	public boolean isSigned;
    }

    public static class IndexedExpression
	extends Expression 
    {
	public Expression indexed;
	public Expression index;

	public void accept(Visitor v) throws ACCError {
	    v.visitIndexedExpression(this);
	}

	public String toString() {
	    return size + "-byte indexed expr" ;
	}
    }

    public static class CallExpression
	extends Expression
    {
	public Expression fn;
	public final List<Expression> args =
	    new LinkedList<Expression>();

	public void accept(Visitor v) throws ACCError {
	    v.visitCallExpression(this);
	}

	public String toString() {
	    return "call expr";
	}
    }

    public static class RegisterExpression
	extends Expression
    {
	/**
	 * A 6502 register.
	 */
	public enum Register 
	{
	    A('A',"$45"),X('X',"$46"),Y('Y',"$47"),
		P('P',"$48"),S('S',"$49");
	    public char name;
	    public String saveAddr;
	    public int offset;
	    Register(char name, String saveAddr) {
		this.name = name;
		this.saveAddr = saveAddr;
	    }
	    public int getOffset() { return offset; }
	    public int getSize() { return 1; }
	    public void setOffset(int offset) {
		this.offset = offset;
	    }
	    public String toString() {
		return "register " + name;
	    }
	}

	public Register register;

	public void accept(Visitor v) throws ACCError {
	    v.visitRegisterExpression(this);
	}

	public String toString() {
	    return "register expr ^" + register.name;
	}
    }

    public static class SetExpression
	extends Expression
    {
	public Expression lhs;
	public Expression rhs;

	public void accept(Visitor v) throws ACCError {
	    v.visitSetExpression(this);
	}

	public String toString() {
	    return "set expr";
	}
    }

    public static class BinopExpression
	extends Expression
    {
	public Expression left;
	public Operator operator;
	public Expression right;

	public enum Operator {
	    SHL("SHL","<<",1),SHR("SHR",">>",1),
	    TIMES("MUL","*",2),DIVIDE("DIV","/",2),
	    PLUS("ADD","+",3),MINUS("SUB","-",3),
	    EQUALS("EQ","=",4),GT("GT",">",4),
	    LT("LT","<",4),GEQ("GEQ",">=",4),
	    LEQ("LEQ","<=",4),AND("AND","AND",5),
	    OR("OR","OR",5),XOR("XOR","XOR",5);
	    public final String name;
	    public final String symbol;
	    public final int precedence;
	    Operator(String name, String symbol, 
		     int precedence) {
		this.name = name;
		this.symbol = symbol;
		this.precedence = precedence;
	    }
	    public String toString() {
		return symbol;
	    }
	}
	public boolean hasSignedness() {
	    switch(operator) {
	    case SHR: case TIMES: case DIVIDE:
	    case GT: case GEQ: case LT: case LEQ:
		return true;
	    }
	    return false;
	}

	public void accept(Visitor v) throws ACCError {
	    v.visitBinopExpression(this);
	}

	public String toString() {
	    return "binop expr " + operator;
	}
    }

    public static class UnopExpression
	extends Expression
    {
	public Operator operator;
	public Expression expr;

	public enum Operator {
	    DEREF("DEREF","@"),NOT("NOT","NOT"),
	    NEG("NEG","-"),INCR("INCR","INCR"),
	    DECR("DECR","DECR");
	    public final String name;
	    public final String symbol;
	    Operator(String name, String symbol) {
		this.name = name;
		this.symbol = symbol;
	    }
	    public String toString() {
		return symbol;
	    }
	}

	public void accept(Visitor v) throws ACCError {
	    v.visitUnopExpression(this);
	}

	public String toString() {
	    return "unop expr " + operator;
	}
    }

    public static class ParensExpression
	extends Expression
    {
	public Expression expr;

	public void accept(Visitor v) throws ACCError {
	    v.visitParensExpression(this);
	}

	public String toString() {
	    return "parens expr";
	}
    }

    public static class Identifier extends Expression {
	/**
	 * The name associated with the identifier
	 */	
	public String name;

	/**
	 * The AST node of the definition to which the name refers
	 */
	public Node def;

	public void accept(Visitor v) throws ACCError {
	    v.visitIdentifier(this);
	}

	public String toString() {
	    return "identifier " + name;
	}
    }

    /**
     * Numeric constant literals
     */
    public static abstract class NumericConstant 
	extends Expression
    {
	public abstract boolean isZero();
	public abstract String valueAsHexString();
	public abstract String valueAsDecString();
    }

    public static class IntegerConstant 
	extends NumericConstant
    {
	public static final BigInteger
	    byteVal = new BigInteger("256");
	
	public BigInteger value;
	public boolean wasHexInSource;
	public boolean isZero() { return value.equals(BigInteger.ZERO); }

	/**
	 * Compute the smallest number of bytes required to hold this
	 * integer in two's complement notation.
	 */
	public int getSize() { 
	    BigInteger unsignedValue = 
		(value.compareTo(BigInteger.ZERO) >= 0)
		? value : value.negate().shiftLeft(1);
	    return computeSize(unsignedValue, BigInteger.ONE,
			       byteVal);
	}

	/**
	 * The one-byte value corresponding to byte idx of this
	 * constant, with low order byte as byte 0.
	 */
	public int valueAtIndex(int idx) {
	    return value.shiftRight(idx*8).
		and(new BigInteger("FF",16)).intValue();
	}

	public String valueAsHexString() {
	    return "$" + value.toString(16).toUpperCase();
	}

	public String valueAsDecString() {
	    return value.toString();
	}

	/**
	 * Compute the smallest exponent k such that 256^k > value
	 */
	private int computeSize(BigInteger value,
				BigInteger exponent,
				BigInteger power) {
	    if (value.compareTo(power) < 0)
		return exponent.intValue();
	    return computeSize(value, exponent.add(BigInteger.ONE),
			       power.multiply(byteVal));
	}

	public void accept(Visitor v)
	    throws ACCError 
	{
	    v.visitIntegerConstant(this);
	}

	public String toString() {
	    if (wasHexInSource) {
		return "int constant " + valueAsHexString();
	    }
	    return "int constant " + valueAsDecString();
	}
    }

    public static class CharConstant 
	extends NumericConstant
    {
	public char value;

	public int getSize() { return 1; }

	public boolean isZero() { return value == 0; }
	
	public void accept(Visitor v) throws ACCError {
	    v.visitCharConstant(this);
	}

	public String valueAsHexString() {
	    return "$" + String.valueOf((int) value).toUpperCase();
	}

	public String valueAsDecString() {
	    return String.valueOf((int) value);
	}

	public String toString() {
	    return "char constant \'" + value + "\'";
	}
    }

    public static String sizeAsString(int size, boolean isSigned) {
	if (isSigned)
	    return String.valueOf(size) + "S";
	return String.valueOf(size);
    }

    /**
     * Generic visitor interface.  To visit a node, override its
     * visitor method.
     */
    public interface Visitor {

	public abstract void
	    visitSourceFile(SourceFile node) 
	    throws ACCError;
	public abstract void
	    visitIncludeDecl(IncludeDecl node) 
	    throws ACCError;
	public abstract void
	    visitConstDecl(ConstDecl node) 
	    throws ACCError;
	public abstract void
	    visitDataDecl(DataDecl node) 
	    throws ACCError;
	public abstract void
	    visitVarDecl(VarDecl node) 
	    throws ACCError;
	public abstract void
	    visitFunctionDecl(FunctionDecl node) 
	    throws ACCError;
	public abstract void
	    visitIfStatement(IfStatement node) 
	    throws ACCError;
	public abstract void
	    visitWhileStatement(WhileStatement node) 
	    throws ACCError;
	public abstract void
	    visitExpressionStatement(ExpressionStatement node) 
	    throws ACCError;
	public abstract void 
	    visitReturnStatement(ReturnStatement node) 
	    throws ACCError;
	public abstract void 
	    visitBlockStatement(BlockStatement node) 
	    throws ACCError;
	public abstract void 
	    visitIndexedExpression(IndexedExpression node)
	    throws ACCError;
	public abstract 
	    void visitCallExpression(CallExpression node) 
	    throws ACCError;
	public abstract void 
	    visitRegisterExpression(RegisterExpression node) 
	    throws ACCError;
	public abstract void 
	    visitSetExpression(SetExpression node) 
	    throws ACCError;
	public abstract void 
	    visitBinopExpression(BinopExpression node) 
	    throws ACCError;
	public abstract void 
	    visitUnopExpression(UnopExpression node) 
	    throws ACCError;
	public abstract void 
	    visitParensExpression(ParensExpression node) 
	    throws ACCError;
	public abstract void 
	    visitIdentifier(Identifier node) 
	    throws ACCError;
	public abstract void
	    visitStringConstant(StringConstant node) 
	    throws ACCError;
	public abstract void
	    visitIntegerConstant(IntegerConstant node)
	    throws ACCError;
	public abstract void 
	    visitCharConstant(CharConstant node) 
	    throws ACCError;
    }
}