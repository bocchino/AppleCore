/**
 * A generic pass that can be run on a function declaration.
 */
package AppleCoreCompiler.AST;

import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

public interface FunctionPass {
    public void runOn(FunctionDecl node)
	throws ACCError;
}