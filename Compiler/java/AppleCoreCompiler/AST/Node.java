/**
 * Class representing an abstract syntax tree node.
 */
package AppleCoreCompiler.AST;

import java.util.*;
import java.math.*;
import AppleCoreCompiler.Syntax.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.AVM.*;

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
     * Whether the node represents an address value.
     */
    public boolean representsAddress() { return false; }

    /**
     * The signedness of the node.
     */
    public boolean isSigned() { return false; }

    /**
     * Look up the definition of an identifier as a variable
     * declaration.
     */
    public VarDecl asVarDecl() { return null; }

    /**
     * Source files
     */
    public static class SourceFile extends Node {
	public String name;
	public String targetFile;
	public boolean includeMode;
	public boolean nativeMode;
	public int origin;
	public List<Declaration> decls = 
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
	/**
	 * Does this declaration have an external definition?
	 */
	public boolean isExternal;
    }

    public static class ConstDecl 
	extends Declaration 
    {
	public String label;
	public Expression expr;
	public int getSize() { 
	    if (expr == null) return 2;
	    return expr.getSize(); 
	}
	public boolean representsAddress() {
	    int size = this.getSize();
	    return (size==1 || size==2) &&
		!this.isSigned();
	}
	public boolean isSigned() { 
	    if (expr == null) return false;
	    return expr.isSigned(); 
	}

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
	    return "data decl " + ((expr==null) ? stringConstant : expr);
	}
    }

    /**
     * String constant literals
     */
    public static class StringConstant 
	extends Node
    {
	public String value;

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
	public boolean representsAddress;
	public boolean isSigned;
	public Expression init;

	public boolean isLocalVariable;
	public boolean isFunctionParam;
	public int offset;

	public void accept(Visitor v) throws ACCError {
	    v.visitVarDecl(this);
	}

	public int getSize() { return size; }
	public boolean representsAddress() { return this.representsAddress; }
	public boolean isSigned() { return this.isSigned; }
	public int getOffset() { return offset; }
	public void setOffset(int offset) {
	    this.offset = offset;
	}

	public VarDecl asVarDecl() {
	    return this;
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
	public boolean representsAddress;
	public String name;
	public final List<VarDecl> params = 
	    new LinkedList<VarDecl>();
	public final List<VarDecl> varDecls =
	    new LinkedList<VarDecl>();
	public Set<RegisterExpression.Register> savedRegs =
	    new TreeSet<RegisterExpression.Register>();
	public final List<Statement> statements =
	    new LinkedList<Statement>();
	public final List<Instruction> instructions =
	    new LinkedList<Instruction>();

	public void accept(Visitor v) 
	    throws ACCError 
	{
	    v.visitFunctionDecl(this);
	}

	/**
	 * Size of the initial stack frame for this function.
	 */
	public int frameSize;

	public int getSize() { return size; }
	public boolean isSigned() { return this.isSigned; }
	public boolean representsAddress() { return this.representsAddress; }

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

    public static class SetStatement
	extends Statement
    {
	public Expression lhs;
	public Expression rhs;

	public void accept(Visitor v) throws ACCError {
	    v.visitSetStatement(this);
	}

	public String toString() {
	    return "set stmt";
	}
    }

    public static class CallStatement
	extends Statement
    {
	public Expression expr;

	public void accept(Visitor v) throws ACCError {
	    v.visitCallStatement(this);
	}

	public String toString() {
	    return "call stmt";
	}
    }

    public static class IncrStatement
	extends Statement
    {
	public Expression expr;

	public void accept(Visitor v) throws ACCError {
	    v.visitIncrStatement(this);
	}

	public String toString() {
	    return "incr stmt";
	}
    }

    public static class DecrStatement
	extends Statement
    {
	public Expression expr;

	public void accept(Visitor v) throws ACCError {
	    v.visitDecrStatement(this);
	}

	public String toString() {
	    return "decr stmt";
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

	/**
	 * Whether this expression represents a pointer value.
	 */
	public boolean representsAddress;

	/**
	 * Whether the value represented by this expression is signed.
	 */
	public boolean isSigned;

	/**
	 * Whether this is a constant-value expression
	 */
	public boolean isConstValExpr() { return false; }

	public int getSize() { return size; }
	public boolean isSigned() { return this.isSigned; }
	public boolean representsAddress() { return this.representsAddress; }
	public boolean isZero() { return false; }
	public boolean isTrue() { return false; }
	public boolean isFalse() { return false; }

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
	public List<Expression> args =
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

	public boolean isConstValExpr() {
	    return left.isConstValExpr() &&
		right.isConstValExpr();
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
	    NEG("NEG","-");
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

	public boolean isConstValExpr() {
	    switch (operator) {
	    case NEG: case NOT:
		return expr.isConstValExpr();
	    }
	    return false;
	}

	public String toString() {
	    return "unop expr " + operator;
	}
    }

    public static class ParensExpression
	extends Expression
    {
	public Expression expr;

	public void accept(Visitor v) 
	    throws ACCError 
	{
	    v.visitParensExpression(this);
	}

	public boolean isConstValExpr() {
	    return expr.isConstValExpr();
	}

	public String toString() {
	    return "parens expr";
	}
    }

    public static class TypedExpression
	extends Expression
    {
	public Expression expr;

	public void accept(Visitor v) 
	    throws ACCError 
	{
	    v.visitTypedExpression(this);
	}

	public boolean isConstValExpr() {
	    return expr.isConstValExpr();
	}

	public String toString() {
	    return "sized expr: " + 
		sizeAsString(size,isSigned);
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

	public boolean isConstValExpr() {
	    return (def instanceof ConstDecl);
	}

	public String toString() {
	    return name;
	}

	public VarDecl asVarDecl() {
	    if (def instanceof VarDecl) {
		return (VarDecl) def;
	    }
	    return null;
	}

    }


    /**
     * Numeric constant literals
     */
    public static abstract class NumericConstant 
	extends Expression
    {
	public abstract String valueAsHexString();
	public abstract String valueAsDecString();
	public abstract BigInteger valueAsBigInteger();
	public abstract BigInteger unsignedValue();
	public boolean isConstValExpr() { return true; }
	public boolean isTrue() {
	    return valueAsBigInteger().and(BigInteger.ONE).equals(BigInteger.ONE);
	}
	public boolean isFalse() {
	    return valueAsBigInteger().and(BigInteger.ONE).equals(BigInteger.ZERO);
	}
    }

    public static class IntegerConstant 
	extends NumericConstant
    {
	public static final BigInteger
	    byteVal = new BigInteger("256");
	
	private BigInteger value;
	public boolean wasHexInSource;
	public boolean isZero() { return value.equals(BigInteger.ZERO); }
	public boolean representsAddress() {
	    return this.size > 0 && this.size <= 2 && !this.isSigned;
	}

	/**
	 * Set the value of this integer constant.  Setting the value
	 * also sets the size and signedness.  The size is the minimum
	 * number of bytes required to represent the value.  The
	 * signedness is signed if the value is negative, otherwise
	 * unsigned.
	 */
	public void setValue(BigInteger value) {
	    // Value must be representable by 255 bytes; if not, strip
	    // off the high-order bits.
	    this.value = value;
	    if (value.compareTo(Token.MAX_INT)>0) {
		value = value.and(Token.MAX_INT);
	    }
	    if (value.compareTo(Token.MAX_INT.negate().shiftRight(1))<=0) {
		value = Token.MAX_INT.add(BigInteger.ONE).subtract(value.negate().and(Token.MAX_INT));
	    }
	    this.size = setSize();
	    this.isSigned = (value.compareTo(BigInteger.ZERO) < 0);
	}

	/**
	 * Compute the smallest number of bytes required to hold this
	 * integer in two's complement notation.
	 */
	private int setSize() { 
	    BigInteger unsignedValue = 
		(value.compareTo(BigInteger.ZERO) >= 0)
		? value : value.negate().shiftLeft(1);
	    return computeSize(unsignedValue, BigInteger.ONE,
			       byteVal);
	}

	public BigInteger valueAsBigInteger() {
	    return value;
	}

	public BigInteger unsignedValue() {
	    return (value.compareTo(BigInteger.ZERO) >= 0)
		? value : BigInteger.valueOf(2).pow(getSize()*8).add(value);
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
	    return "$" + 
		unsignedValue().toString(16).toUpperCase();
	}

	public String valueAsDecString() {
	    return unsignedValue().toString();
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
	    return "int constant " + valueAsHexString();
	}

	public int hashCode() {
	    return value.hashCode();
	}

	public boolean equals(Object o) {
	    if (!(o instanceof IntegerConstant)) return false;
	    IntegerConstant ic = (IntegerConstant) o;
	    return ic.value.equals(this.value);
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

	public BigInteger valueAsBigInteger() {
	    return BigInteger.valueOf(value);
	}

	public BigInteger unsignedValue() {
	    return valueAsBigInteger();
	}

	public String valueAsHexString() {
	    return "$" + Integer.toHexString((int) value).toUpperCase();
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
	    visitSetStatement(SetStatement node) 
	    throws ACCError;
	public abstract void
	    visitIncrStatement(IncrStatement node) 
	    throws ACCError;
	public abstract void
	    visitDecrStatement(DecrStatement node) 
	    throws ACCError;
	public abstract void
	    visitCallStatement(CallStatement node) 
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
	public abstract void
	    visitCallExpression(CallExpression node) 
	    throws ACCError;
	public abstract void 
	    visitRegisterExpression(RegisterExpression node) 
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
	    visitTypedExpression(TypedExpression node)
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