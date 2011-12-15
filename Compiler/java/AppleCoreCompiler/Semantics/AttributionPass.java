/**
 * Scan the AST and fill in the AST nodes of symbol defs corresponding
 * to their uses.
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Syntax.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.Warnings.*;

import java.util.*;

public class AttributionPass 
    extends ASTScanner 
    implements Pass
{

    private final Warner warner;
    public AttributionPass(Warner warner) {
	this.warner = warner;
    }

    private boolean debug = false;

    private void printStatus(String msg, Node node) {
	if (debug) {
	    System.out.println("Line " + node.lineNumber 
			       + ": " + msg + " " + node);
	}
    }

    /**
     * A map for symbols in the global namespace
     */
    Map<String,Node> globalSymbols = new HashMap<String,Node>();

    /**
     * A map for symbols in the function-local namespace
     */
    Map<String,Node> localSymbols = new HashMap<String,Node>();

    /**
     * Attribute the AST
     */
    public void runOn(SourceFile sourceFile) 
	throws ACCError
    {
	// Enter the built-in functions
	enterBuiltInFunctions();
	// Enter all the top-level declarations except constant decls,
	// which may not be forward declared.
	for (Declaration decl : sourceFile.decls) {
	    if (!(decl instanceof ConstDecl)) {
		insertDecl.insert(decl, globalSymbols);
	    }
	}
	// Attribute the source file
	scan(sourceFile);
    }

    private void enterBuiltInFunctions() 
	throws ACCError
    {
	try {
	    String appleCore = System.getenv("APPLECORE");
	    if (appleCore == null) {
		throw new SemanticError("environment variable APPLECORE not set");
	    }
	    String builtInFns = appleCore + 
		"/Compiler/java/AppleCoreCompiler/Semantics/BUILT.IN.FNS.ac";
	    Parser parser = new Parser(builtInFns);
	    SourceFile sourceFile = parser.parse();
	    for (Declaration decl : sourceFile.decls) {
		insertDecl.insert(decl, globalSymbols);
	    }
	}
	catch (NullPointerException e) {
	    throw new SemanticError("could not read built-in function decls");
	}
    }

    private InsertDecl insertDecl = new InsertDecl();
    private class InsertDecl extends NodeVisitor {

	Map<String,Node> map;
	void insert(Declaration decl, 
		    Map<String,Node> map) 
	    throws ACCError
	{
	    this.map = map;
	    decl.accept(this);
	}
	public void visitConstDecl(ConstDecl node) 
	    throws SemanticError
	{
	    printStatus("Adding symbol for ", node);
	    addMapping(node.label,node,map);
	}
	public void visitDataDecl(DataDecl node) 
	    throws SemanticError
	{
	    if (node.label != null) {
		printStatus("Adding symbol for ", node);
		addMapping(node.label,node,map);
	    }
	}
	public void visitVarDecl(VarDecl node) 
	    throws SemanticError
	{
	    printStatus("Adding symbol for ", node);
	    addMapping(node.name,node,map);
	}
	public void visitFunctionDecl(FunctionDecl node) 
	    throws SemanticError
	{
	    printStatus("Adding symbol for ", node);
	    addMapping(node.name,node,map);
	}
    }

    /**
     * Add a (String,Node) mapping to the map and check for duplicate
     * definition.
     */
    void addMapping(String name, Node node, Map<String,Node> map)
	throws SemanticError {
	Node priorEntry = map.put(name, node);
	// Check for symbol redefinition
	if (priorEntry != null && priorEntry != node) {
	    throw new SemanticError(name + " already defined at line " +
				    priorEntry.lineNumber, node);
	}
    }

    public void visitConstDecl(ConstDecl node)
	throws ACCError
    {
	printStatus("Attributing const decl ", node);
	// Insert the decl in the constant namespace now
	insertDecl.insert(node, globalSymbols);
	// Attribute the expression
	super.visitConstDecl(node);	
    }

    public void visitFunctionDecl(FunctionDecl node) 
	throws ACCError
    {
	printStatus("Attributing function decl ", node);
	// Insert the parameters into the local namespace
	for (Declaration decl : node.params) {
	    insertDecl.insert(decl, localSymbols);
	}

	// Attribute the function
	super.visitFunctionDecl(node);

	// Reset the local namespace
	localSymbols.clear();
    }

    public void visitVarDecl(VarDecl node) 
	throws ACCError
    {
	if (node.isLocalVariable) {
	    printStatus("Attributing ",node);
	    addMapping(node.name, node, localSymbols);
	} else {
	    if (node.init != null) {
		if (!node.init.isConstValExpr()) {
		    throw new SemanticError("constant value expression required",
					    node);
		}
	    }
	}
	super.visitVarDecl(node);
    }

    private Node findSymbol(String name) {
	Node result = localSymbols.get(name);
	if (result == null)
	    result = globalSymbols.get(name);
	return result;
    }

    public void visitIdentifier(Identifier node) 
	throws ACCError
    {
	printStatus("Attributing ",node);
	node.def = findSymbol(node.name);
	if (node.def == null) {
	    throw new SemanticError(node.name + " not defined",
				    node);
	}
    }
}