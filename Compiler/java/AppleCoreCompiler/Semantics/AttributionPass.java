/**
 * Scan the AST and fill in the AST nodes of symbol defs corresponding
 * to their uses.
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Syntax.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.Transforms.*;
import AppleCoreCompiler.Warnings.*;

import java.util.*;
import java.io.*;

public class AttributionPass 
    extends ASTScanner 
    implements Pass
{

    private final List<String> declFiles;

    public AttributionPass(List<String> declFiles) {
	this.declFiles = declFiles;
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
    private final Map<String,Node> globalSymbols = 
	new HashMap<String,Node>();

    /**
     * A map for symbols in the function-local namespace
     */
    private final Map<String,Node> localSymbols = 
	new HashMap<String,Node>();

    /**
     * The source file being attributed
     */
    private SourceFile sourceFile;

    /**
     * Attribute the AST
     */
    public void runOn(SourceFile sourceFile) 
	throws ACCError
    {
	this.sourceFile = sourceFile;
	// Enter the built-in functions
	enterBuiltInFunctions();
	// Enter decls from files specified on command line
	enterImportedDecls();
	// Enter all the top-level declarations from the source file
	// except constant decls, which may not be forward declared.
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
	String appleCore = System.getenv("APPLECORE");
	if (appleCore == null) {
	    throw new SemanticError("environment variable APPLECORE not set");
	}
	String builtInFns = appleCore + 
	    "/Compiler/java/AppleCoreCompiler/Semantics/BUILT.IN.FNS.ac";
	enterDeclsFrom(builtInFns);
    }

    private void enterImportedDecls() 
	throws ACCError
    {
	for (String declFile : declFiles) {
	    if (!declFile.equals(sourceFile.name)) {
		enterDeclsFrom(declFile);
	    }
	}
    }

    private void enterDeclsFrom(String importFileName) 
	throws ACCError
    {
	Parser parser = new Parser(importFileName);
	try {
	    SourceFile importFile = parser.parse();
	    // Make a fresh list of declarations
	    List<Declaration> newDecls
		= new LinkedList<Declaration>();
	    for (Declaration decl : importFile.decls) {
		insertDecl.insert(decl, globalSymbols);
		if (decl instanceof ConstDecl) {
		    // Collect imported constant decls, in the order
		    // seen
		    newDecls.add(decl);
		}
	    }
	    // Prepend imported constant decls, so they can be
	    // attributed, error-checked, and evaluated along with the
	    // rest.
	    newDecls.addAll(sourceFile.decls);
	    // Sub in the new list
	    sourceFile.decls = newDecls;
	}
	catch (FileNotFoundException e) {
	    System.err.println("acc: file " + importFileName + 
			       " not found");
	}
	catch (IOException e) {
	    System.err.println("acc: I/O exception");
	}
    }

    private InsertDecl insertDecl = new InsertDecl();
    private class InsertDecl extends NodeVisitor {

	private Map<String,Node> map;
	void insert(Declaration decl, Map<String,Node> map) 
	    throws ACCError
	{
	    this.map = map;
	    decl.accept(this);
	}
	public void visitConstDecl(ConstDecl node) 
	    throws SemanticError, ACCError
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
	    String sourceFileName = node.sourceFileName;
	    StringBuffer sb = 
		new StringBuffer(name + " already defined at line " +
				 priorEntry.lineNumber);
	    if (!sourceFileName.equals(sourceFile.name)) {
		sb.append(" of " + sourceFileName);
	    }
	    throw new SemanticError(sb.toString(), node);
	}
    }

    public void visitConstDecl(ConstDecl node)
	throws ACCError
    {
	printStatus("Attributing const decl ", node);
	// Attribute the expression
	super.visitConstDecl(node);	
	// Insert the decl in the constant namespace now
	insertDecl.insert(node, globalSymbols);
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