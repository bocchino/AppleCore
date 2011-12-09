/**
 * Check that at every function call site where a prototype is
 * available for the called function, the number of args matches the
 * number of params.
 */
package AppleCoreCompiler.Semantics;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

public class FunctionCallPass
    extends ASTScanner 
    implements Pass
{

    public boolean debug;

    public void runOn(Program program) 
	throws ACCError
    {
	scan(program);
    }

    public void visitCallExpression(CallExpression node) 
	throws ACCError
    {
	super.visitCallExpression(node);
	if (!argsMatch(node)) {
	    throw new SemanticError("wrong number of function args",
				    node);
	}
    }

    private boolean argsMatch(CallExpression node) 
	throws ACCError
    {
	return argsChecker.check(node);
    }
    private ArgsChecker argsChecker=
	new ArgsChecker();
    private class ArgsChecker extends NodeVisitor {

	private boolean result = true;
	private CallExpression site;
	
	public boolean check(CallExpression node) 
	    throws ACCError
	{
	    site = node;
	    if (node.fn instanceof Identifier) {
		Identifier id = (Identifier) node.fn;
		Node def = id.def;
		if (def != null) def.accept(this);
	    }
	    return result;
	}
	
	public void visitFunctionDecl(FunctionDecl node) {
	    result = (site.args.size() == node.params.size());
	}
    }

}