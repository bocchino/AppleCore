package AppleCoreCompiler.Syntax;

import java.io.*;
import java.util.*;
import java.math.*;
import AppleCoreCompiler.AST.Node;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.Syntax.*;

public class Parser {

    public static final BigInteger MAX_SIZE = 
	new BigInteger("255");

    /**
     * Construct a new Parser object with the specified input file
     */
    public Parser(String inputFileName) {
	this.inputFileName = inputFileName;
    }

    /**
     * The input file name
     */
    private String inputFileName;

    /**
     * Scanner to provide the token stream
     */
    private Scanner scanner;

    /**
     * Main method for testing the parser
     */

    public static void main(String args[]) {
	Parser parser = null;
	try {
	    parser = new Parser(args[0]);
	    parser.debug = true;
	    System.err.println("Parsing " + args[0] + "...");
	}
	catch (ArrayIndexOutOfBoundsException e) {
	    System.err.println("usage: java Parser [filename]");
	    System.exit(1);
	}
	Program program = parser.parse();
	// Print out the AST
    }

    /**
     * Parse an AppleCore source program.
     */
    public Program parse() {
	Program program = null;
	FileReader fr = null;
	try {
	    try {
		fr = new FileReader(inputFileName);
		scanner = new Scanner(new BufferedReader(fr));
		scanner.getNextToken();
		program = parseProgram();
	    }
	    finally {
		if (fr != null) fr.close();
	    }
	}
	catch (SyntaxError e) {
	    System.err.print("line " + e.getLineNumber() + " of " + 
			     inputFileName +": ");
	    System.err.println(e.getMessage());
	}
	catch (FileNotFoundException e) {
	    System.err.println("file " + inputFileName + " not found");
	}
	catch (IOException e) {
	    System.err.println("I/O exception");
	}
	return program;
    }

    /**
     * Program ::= Decl*
     */
    private Program parseProgram() 
	throws SyntaxError, IOException
    {
	Program program = new Program();
	setLineNumberOf(program);
	while (scanner.getCurrentToken() != Token.END) {
	    Declaration decl = parseDecl();
	    if (decl == null) break;
	    program.decls.add(decl);
	}
	return program;
    }

    public boolean debug = false;
    private void printStatus(String s) {
	if (debug) {
	    System.out.print("Line " + scanner.getCurrentToken().lineNumber);
	    System.out.println(": " + s);
	}
    }

    private void printStatus() {
	printStatus("parsed " + scanner.getCurrentToken());
    }

    /**
     * Decl ::= Const-Decl | Data-Decl | Var-Decl | Fn-Decl 
     *          | Include-Decl
     */
    private Declaration parseDecl() 
	throws SyntaxError, IOException
    {
	Declaration result = null;
	Token token = scanner.getCurrentToken();
	switch (token) {
	case INCLUDE:
	    printStatus();
	    result = parseIncludeDecl();
	    break;
	case CONST:
	    printStatus();
	    result = parseConstDecl();
	    break;
	case DATA:
	    printStatus();
	    result = parseDataDecl();
	    break;
	case VAR:
	    printStatus();
	    result = parseVarDecl(false);
	    break;
	case FN:
	    printStatus();
	    scanner.getNextToken();
	    result = parseFunctionDecl();
	    break;
	default:
	    throw SyntaxError.expected("declaration",
				       token);
	}
	return result;
    }

    /**
     * Include-Decl ::= INCLUDE String-Const ';'
     */
    private IncludeDecl parseIncludeDecl() 
	throws SyntaxError, IOException
    {
	IncludeDecl includeDecl = new IncludeDecl();
	setLineNumberOf(includeDecl);
	expectAndConsume(Token.INCLUDE);
	expect(Token.STRING_CONST);
	includeDecl.filename = parseStringConstant().value;
	expectAndConsume(Token.SEMI);
	return includeDecl;
    }

    /**
     * Const-Decl ::= CONST Ident [Numeric-Const] ';'
     */
    private ConstDecl parseConstDecl() 
	throws SyntaxError, IOException
    {
	ConstDecl constDecl = new ConstDecl();
	setLineNumberOf(constDecl);
	expectAndConsume(Token.CONST);
	constDecl.label = parseName();
	if (scanner.getCurrentToken() != Token.SEMI) {
	    constDecl.constant = parseNumericConstant();
	}
	expectAndConsume(Token.SEMI);
	return constDecl;
    }

    /**
     * Data-Decl ::= DATA [Ident] Const ';'
     */
    private DataDecl parseDataDecl() 
	throws SyntaxError, IOException
    {
	DataDecl dataDecl = new DataDecl();
	setLineNumberOf(dataDecl);
	expectAndConsume(Token.DATA);
	dataDecl.label = parsePossibleName();
	switch (scanner.getCurrentToken()) {
	case STRING_CONST:
	    dataDecl.constant = parseStringConstant();
	    // Check for unterminated string
	    if (scanner.getCurrentToken() == Token.BACKSLASH) {
		scanner.getNextToken();
	    }
	    else {
		dataDecl.isTerminatedString = true;
	    }
	    break;
	default:
	    dataDecl.constant = parseNumericConstant();
	    break;
	}
	expectAndConsume(Token.SEMI);
	return dataDecl;
    }

    /**
     * Var-Decl ::= VAR Ident ':' Size ['S'] ['=' Expr] ';'
     */
    private VarDecl parseVarDecl(boolean isLocalVariable)
	throws SyntaxError, IOException
    {
	printStatus("parsing variable declaration");
	VarDecl varDecl = new VarDecl();
	setLineNumberOf(varDecl);
	expectAndConsume(Token.VAR);
	varDecl.isLocalVariable = isLocalVariable;
	varDecl.name = parseName();
	expectAndConsume(Token.COLON);
	varDecl.size = parseSize();
	varDecl.isSigned = parseIsSigned();
	if (scanner.getCurrentToken() == Token.EQUALS) {
	    scanner.getNextToken();
	    varDecl.init = parseExpression();
	}
	expectAndConsume(Token.SEMI);
	return varDecl;
    }

    /**
     * Fn-Decl ::= FN [':' Size ['S']] Ident '(' Params ')' Fn-Body
     * Fn-Body ::= '{' Var-Decl* Stmt* '}' | ';'
     */
    private FunctionDecl parseFunctionDecl()
	throws SyntaxError, IOException
    {
	FunctionDecl functionDecl = new FunctionDecl();
	setLineNumberOf(functionDecl);
	if (scanner.getCurrentToken() == Token.COLON) {
	    scanner.getNextToken();
	    functionDecl.size = parseSize();
	    functionDecl.isSigned = parseIsSigned();
	}
	functionDecl.name = parseName();
	expectAndConsume(Token.LPAREN);
	parseFunctionParams(functionDecl.params);
	expectAndConsume(Token.RPAREN);
	if (scanner.getCurrentToken() == Token.LBRACE) {
	    scanner.getNextToken();
	    parseVarDecls(functionDecl.varDecls,true);
	    parseStatements(functionDecl.statements);
	    expectAndConsume(Token.RBRACE);
	}
	else {
	    expectAndConsume(Token.SEMI);
	    functionDecl.isExternal = true;
	}
	return functionDecl;
    }

    /**
     * Params ::= [ Param (',' Param)* ]
     * Param  ::= IDENT ':' Size ['S']
     */
    private void parseFunctionParams(List<VarDecl> params)
	throws SyntaxError, IOException
    {
	String name = parsePossibleName();
	while (name != null) {
	    printStatus("parsing function parameter");
	    VarDecl param = new VarDecl();
	    param.isLocalVariable = true;
	    param.isFunctionParam = true;
	    setLineNumberOf(param);
	    param.name = name;
	    expectAndConsume(Token.COLON);
	    param.size = parseSize();
	    param.isSigned = parseIsSigned();
	    params.add(param);
	    name = null;
	    if (scanner.getCurrentToken() == Token.COMMA) {
		scanner.getNextToken();
		name = parseName();
	    }
	}
    }

    /**
     * Var-Decls ::= Var-Decl*
     */
    private void parseVarDecls(List<VarDecl> varDecls,
			       boolean isLocalVariable) 
	throws SyntaxError, IOException
    {
	while (scanner.getCurrentToken() == Token.VAR) {
	    VarDecl varDecl = parseVarDecl(isLocalVariable);
	    varDecls.add(varDecl);
	}
    }

    /**
     * Stmt-List ::= Stmt*
     */
    private void parseStatements(List<Statement> statements) 
	throws SyntaxError, IOException
    {
	while (scanner.getCurrentToken() != Token.RBRACE) {
	    Statement statement = parseStatement();
	    statements.add(statement);
	} 
    }

    /**
     * Stmt ::= If-Stmt | While-Stmt | Return-Stmt | Block-Stmt |
     *          Expr-Stmt
     */
    private Statement parseStatement() 
	throws SyntaxError, IOException
    {
	Statement result = null;
	Token token = scanner.getCurrentToken();
	switch (token) {
	case IF:
	    result = parseIfStatement();
	    break;
	case WHILE:
	    result = parseWhileStatement();
	    break;
	case RETURN:
	    result = parseReturnStatement();
	    break;
	case LBRACE:
	    result = parseBlockStatement();
	    break;
	default:
	    result = parseExpressionStatement();
	    break;
	}
	return result;
    }

    /**
     * If-Stmt ::= IF '(' Expr ')' Stmt [ELSE Stmt]
     */
    private IfStatement parseIfStatement() 
	throws SyntaxError, IOException
    {
	IfStatement ifStmt = new IfStatement();
	setLineNumberOf(ifStmt);
	expectAndConsume(Token.IF);
	expectAndConsume(Token.LPAREN);
	ifStmt.test = parseExpression();
	expectAndConsume(Token.RPAREN);
	ifStmt.thenPart = parseStatement();
	if (scanner.getCurrentToken() == Token.ELSE) {
	    scanner.getNextToken();
	    ifStmt.elsePart = parseStatement();
	}
	return ifStmt;
    }

    /**
     * While-Stmt ::= WHILE '(' Expr ')' Stmt
     */
    private WhileStatement parseWhileStatement() 
	throws SyntaxError, IOException
    {
	WhileStatement whileStmt = new WhileStatement();
	setLineNumberOf(whileStmt);
	expectAndConsume(Token.WHILE);
	expectAndConsume(Token.LPAREN);
	whileStmt.test = parseExpression();
	expectAndConsume(Token.RPAREN);
	whileStmt.body = parseStatement();
	return whileStmt;
    }

    /**
     * Return-Stmt ::= RETURN [Expr] ';'
     */ 
    private ReturnStatement parseReturnStatement()
	throws SyntaxError, IOException
    {
	ReturnStatement returnStmt = new ReturnStatement();
	setLineNumberOf(returnStmt);
	expectAndConsume(Token.RETURN);
	if (scanner.getCurrentToken() != Token.SEMI) {
	    returnStmt.expr = parseExpression();
	}
	expectAndConsume(Token.SEMI);
	return returnStmt;
    }

    /**
     * Block-Stmt ::= '{' Stmt-List '}'
     */
    private BlockStatement parseBlockStatement() 
	throws SyntaxError, IOException
    {
	BlockStatement blockStmt = new BlockStatement();
	setLineNumberOf(blockStmt);
	expectAndConsume(Token.LBRACE);
	parseStatements(blockStmt.statements);
	expectAndConsume(Token.RBRACE);
	return blockStmt;
    }

    /**
     * Expr-Stmt ::= Expr ';'
     */
    private Statement parseExpressionStatement() 
	throws SyntaxError, IOException
    {
	ExpressionStatement exprStmt = new ExpressionStatement();
	setLineNumberOf(exprStmt);
	Expression expr = parseExpression();
	exprStmt.expr = expr;
	expectAndConsume(Token.SEMI);
	return exprStmt;
    }

    /**
     * Expr ::= LValue-Expr | Call-Expr | Set-Expr | 
     *          Binop-Expr | Unop-Expr | Numeric-Const | 
     *          Parens-Expr 
     */
    private Expression parseExpression() 
	throws SyntaxError, IOException
    {
	return parseExpression(false);
    }

    /**
     * LValue-Expr ::= Ident | Indexed-Expr | Register-Expr
     */
    private Expression parseLValueExpression()
	throws SyntaxError, IOException
    {
	return parseExpression(true);
    }

    /**
     * Parse an expression.
     *
     * @param lvalue   If set to true, only an lvalue expression
     *                 will be parsed.  A non-lvalue expression
     *                 causes an error.
     */
    private Expression parseExpression(boolean lvalue) 
	throws SyntaxError, IOException
    {
	Token token = scanner.getCurrentToken();
	Expression result = null;
	switch (token) {
	case IDENT:
	    result = parseIdentPrefixExpr(lvalue);
	    break;
	case CARET:
	    result = parseRegisterExpr();
	    break;
	case SET:
	    if (!lvalue) result = parseSetExpr();
	    break;
	case LPAREN:
	    result = parseParensExpr();
	    break;
	case CHAR_CONST:
	case INT_CONST:
	    if (!lvalue) result = parseConstantExpression();
	    break;
	default:
	    if (!lvalue) {
		Node.UnopExpression.Operator op =
		    getUnaryOperatorFor(token);
		if (op != null) {
		    result = parseUnopExpr(op);
		}
	    }
	    break;
	}
	if (result != null && !lvalue) {
	    Node.BinopExpression.Operator op =
		getBinaryOperatorFor(scanner.getCurrentToken());
	    if (op != null) {
		scanner.getNextToken();
		result = parseBinopExpression(result, op);
	    }
	}
	if (result == null) {
	    String msg = lvalue ? "lvalue expression" : "expression";
	    throw SyntaxError.expected(msg, token);
	}
	return result;
    }

    /**
     * Parse a non-binop expression starting with an identifier, i.e.,
     * Ident, Inexed-Expr, or Call-Expr.
     *
     * @param lvalue   Return null if lvalue is true and a
     *                 call expression is found.
     */
    private Expression parseIdentPrefixExpr(boolean lvalue) 
	throws SyntaxError, IOException
    {
	Identifier ident = parseIdentifier();
	Token token = scanner.getCurrentToken();
	switch (token) {
	case LBRACKET:
	    return parseIndexedExpression(ident);
	case LPAREN:
	    return lvalue ? null : parseCallExpression(ident);
	default:
	    return ident;
	}
    }

    /**
     * IndexedExpr ::= Ident '[' Expr,Size ']'
     */
    private IndexedExpression parseIndexedExpression(Identifier indexed) 
	throws SyntaxError, IOException
    {
	IndexedExpression indexedExp = new IndexedExpression();
	setLineNumberOf(indexedExp);
	indexedExp.indexed = indexed;
	expectAndConsume(Token.LBRACKET);
	indexedExp.index = parseExpression();
	expectAndConsume(Token.COMMA);
	indexedExp.size = parseSize();
	expectAndConsume(Token.RBRACKET);
	return indexedExp;
    }

    /**
     * Call-Expr ::= Ident '(' Args ')'
     * Args      ::= [Expr [',' Expr]*]
     */
    private CallExpression parseCallExpression(Identifier name) 
	throws SyntaxError, IOException
    {
	CallExpression callExp = new CallExpression();
	callExp.lineNumber = name.lineNumber;
	callExp.name = name;
	expectAndConsume(Token.LPAREN);
	if (scanner.getCurrentToken() == Token.RPAREN) {
	    printStatus();
	    scanner.getNextToken();
	    return callExp;
	}
	while (true) {
	    callExp.args.add(parseExpression());
	    if (scanner.getCurrentToken() != Token.COMMA)
		break;
	    scanner.getNextToken();
	}
	expectAndConsume(Token.RPAREN);
	return callExp;
    }

    /**
     * Register-Expr ::= '^' Reg-Name
     * Reg-Name      ::= 'X' | 'Y' | 'S' | 'P' | 'A'
     */
    private RegisterExpression parseRegisterExpr() 
	throws SyntaxError, IOException
    {
	RegisterExpression regExp = new RegisterExpression();
	setLineNumberOf(regExp);
	expectAndConsume(Token.CARET);
	Token token = scanner.getCurrentToken();
	Identifier ident = parseIdentifier();
	if (ident.name.length() == 1) {
	    char name = ident.name.charAt(0);
	    for (RegisterExpression.Register reg : 
		     RegisterExpression.Register.values()) {
		if (reg.name == name) {
		    regExp.register = reg;
		    return regExp;
		}
	    }
	}
	throw SyntaxError.expected("register name", token);
    }

    /**
     * Set-Expr ::= SET LValue-Expr '=' Expr
     */
    private SetExpression parseSetExpr() 
	throws SyntaxError, IOException
    {
	SetExpression setExp = new SetExpression();
	setLineNumberOf(setExp);
	expectAndConsume(Token.SET);
	setExp.lhs = parseLValueExpression();
	expectAndConsume(Token.EQUALS);
	setExp.rhs = parseExpression();
	return setExp;
    }

    /**
     * Unop-Expr ::= Unop Expr
     * Unop      ::= '@' | 'NOT' | '-' | 'INCR' | 'DECR'
     */
    private UnopExpression parseUnopExpr(Node.UnopExpression.Operator op) 
	throws SyntaxError, IOException
    {
	UnopExpression unopExpr = new UnopExpression();
	setLineNumberOf(unopExpr);
	unopExpr.operator = op;
	scanner.getNextToken();
	unopExpr.expr = parseExpression();
	return unopExpr;
    }

    /**
     * Return the unary operator corresponding to the token, null if
     * none.
     */
    private Node.UnopExpression.Operator getUnaryOperatorFor(Token token) {
	for (Node.UnopExpression.Operator op : 
		 Node.UnopExpression.Operator.values()) {
	    if (token.stringValue.equals(op.symbol))
		return op;
	}
	return null;
    }

    /**
     * Parens-Expr ::= '(' Expr ')'
     */
    private ParensExpression parseParensExpr() 
	throws SyntaxError, IOException
    {
	ParensExpression parensExp = new ParensExpression();
	setLineNumberOf(parensExp);
	expectAndConsume(Token.LPAREN);
	parensExp.expr = parseExpression();
	expectAndConsume(Token.RPAREN);
	return parensExp;
    }

    /**
     * Constant-Expr ::= Numeric-Const
     */
    private ConstantExpression parseConstantExpression() 
	throws SyntaxError, IOException
    {
	ConstantExpression constantExp = new ConstantExpression();
	setLineNumberOf(constantExp);
	Constant constant = parseNumericConstant();
	constantExp.value = constant;
	return constantExp;
    }

    /**
     * Binop-Expr ::= Expr Binop Expr
     * Binop      ::= '=' | '>' | '<' | '<=' | '>=' | 
     *                'AND' | 'OR' | 'XOR' | '+' | '-' | 
     *                '*' | '/' | '<<' | '>>'
     */
    private BinopExpression parseBinopExpression(Expression left, 
						 Node.BinopExpression.Operator op) 
	throws SyntaxError, IOException
    {
	BinopExpression result = new BinopExpression();
	setLineNumberOf(result);
	result.left = left;
	result.operator = op;
	Expression right = parseExpression();
	if (right instanceof BinopExpression) {
	    BinopExpression binop = (BinopExpression) right;
	    if (op.precedence < binop.operator.precedence) {
		// Switch precedence
		result.right = binop.left;
		binop.left = result;
		result = binop;
	    } else {
		result.right = right;
	    }
	} else {
	    result.right = right;
	}
	return result;
    }

    /**
     * Return the binary operator corresponding to the token, null if
     * none.
     */
    private Node.BinopExpression.Operator getBinaryOperatorFor(Token token) {
	for (Node.BinopExpression.Operator op : 
		 Node.BinopExpression.Operator.values()) {
	    if (token.stringValue.equals(op.symbol))
		return op;
	}
	return null;
    }

    /**
     * Parse a size.
     */
    private int parseSize() 
	throws SyntaxError, IOException
    {
	IntegerConstant intConstant = parseIntConstant();
	if (intConstant.value.compareTo(BigInteger.ZERO) < 0 ||
	    intConstant.value.compareTo(MAX_SIZE) > 0) {
	    throw new SyntaxError(intConstant + " out of range",
				  scanner.getLineNumber());
	}
	return intConstant.value.intValue();
    }

    /**
     * Look for an 'S' indicating a signed value.  If it's there
     * return true and advance the token stream; otherwise return
     * false.
     */
    private boolean parseIsSigned() 
	throws SyntaxError, IOException
    {
	Token token = scanner.getCurrentToken();
	if (token == Token.IDENT && token.stringValue.equals("S")) {
	    scanner.getNextToken();
	    return true;
	}
	return false;
    }

    /**
     * Parse and return an identifier if it's there.  If not, return
     * null.
     */
    private Identifier parsePossibleIdentifier() 
	throws SyntaxError, IOException
    {
	Token token = scanner.getCurrentToken();
	if (token == Token.IDENT) {
	    return parseIdentifier();
	}
	return null;
    }

    /**
     * Expect and consume an identifier token and return an AST node
     * for that token.
     */
    private Identifier parseIdentifier() 
	throws SyntaxError, IOException
    {
	expect(Token.IDENT);
	Token token = scanner.getCurrentToken();
	Identifier ident = new Identifier();
	setLineNumberOf(ident);
	ident.name = token.stringValue;
	scanner.getNextToken();
	return ident;
    }

    /**
     * Parse and return an identifier if it's there.  If not, return
     * null.
     */
    private String parsePossibleName() 
	throws SyntaxError, IOException
    {
	Token token = scanner.getCurrentToken();
	if (token == Token.IDENT) {
	    return parseName();
	}
	return null;
    }

    /**
     * Parse an identifier token and extract the name.
     */
    private String parseName() 
	throws SyntaxError, IOException
    {
	expect(Token.IDENT);
	Token token = scanner.getCurrentToken();
	String name = token.stringValue;
	scanner.getNextToken();
	return name;
    }

    /**
     * Parse a string constant
     */
    private StringConstant parseStringConstant()
	throws SyntaxError, IOException
    {
	StringConstant stringConstant = new StringConstant();
	setLineNumberOf(stringConstant);
	Token token = scanner.getCurrentToken();
	stringConstant.value = token.getStringValue();
	scanner.getNextToken();
	return stringConstant;
    }

    /**
     * Numeric-Const ::= Int-Const | Char-Const
     */
    private Constant parseNumericConstant() 
	throws SyntaxError, IOException
    {
	IntegerConstant intConstant = parsePossibleIntConstant();
	if (intConstant != null) return intConstant;
	Token token = scanner.getCurrentToken();
	if (token != Token.CHAR_CONST) {
	    throw SyntaxError.expected("numeric constant", token);
	}
	printStatus();
	CharConstant charConstant = new CharConstant();
	setLineNumberOf(charConstant);
	charConstant.value = token.stringValue.charAt(0);
	scanner.getNextToken();
	return charConstant;
    }

    /**
     * Parse an integer constant.
     */
    private IntegerConstant parseIntConstant() 
	throws SyntaxError, IOException
    {
	IntegerConstant result = parsePossibleIntConstant();
	if (result == null)
	    throw SyntaxError.expected ("integer constant", 
					scanner.getCurrentToken());
	return result;
    }

    /**
     * Try to get an integer constant.  Return an integer constant
     * object on success, null on failure.
     */
    private IntegerConstant parsePossibleIntConstant() 
	throws SyntaxError, IOException
    {
	Token token = scanner.getCurrentToken();
	IntegerConstant result = null;
	switch (token) {
	case INT_CONST:
	    result = new IntegerConstant();
	    result.wasHexInSource = token.wasHexInSource;
	    break;
	}
	if (result != null) {
	    printStatus();
	    setLineNumberOf(result);
	    result.value = token.getNumberValue();
	    scanner.getNextToken();
	}
	return result;
    }

    /**
     * Check that the current token is the expected one; if not,
     * report an error
     */
    private void expect(Token token) 
	throws SyntaxError 
    {
	Token currentToken = scanner.getCurrentToken();
	if (token != currentToken)
	    throw SyntaxError.expected(token.expectedString(),
				       currentToken);
	printStatus();
    }

    /**
     * Check for the expected token, then consume it
     */
    private Token expectAndConsume(Token token)
	throws SyntaxError, IOException {
	expect(token);
	return scanner.getNextToken();
    }

    /**
     * Set the line number of an AST node to the current line
     * recorded in the scanner.
     */
    private void setLineNumberOf(Node node) {
	node.lineNumber = scanner.getLineNumber();
    }

}
