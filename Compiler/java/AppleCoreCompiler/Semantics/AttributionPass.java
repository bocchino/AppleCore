/**
 * Scan the AST and fill in the AST nodes of symbol defs corresponding
 * to their uses.
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
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
	// Enter the top-level declarations
	for (Declaration decl : sourceFile.decls) {
	    insertDecl.insert(decl, globalSymbols);
	}
	// Attribute the source file
	scan(sourceFile);
    }

    private InsertDecl insertDecl = new InsertDecl();
    private class InsertDecl extends NodeVisitor {

	Map<String,Node> map;
	Node conflictingEntry;
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
	    printStatus("Adding symbol for ", node);
	    addMapping(node.label,node,map);
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
	    if (priorEntry.lineNumber > 0) {
		// If the node has line number > 0, then there is an
		// illegal redefinition.
		throw new SemanticError(name + " already defined at line " +
					priorEntry.lineNumber, node);
	    }
	    else {
		// If the node has line number 0, then the user is
		// redefining a built-in symbol.  Allow, but warn.
		warner.warn("redefinition of built-in symbol " + name, node);
	    }
	}
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
		if (!(node.init instanceof NumericConstant)) {
		    // TODO: Arithmetic expressions involving
		    // constants should be allowed.
		    throw new SemanticError("non-constant initializer not implemented",
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