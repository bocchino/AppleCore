/**
 * A generic pass that can be run on a program.
 */
package AppleCoreCompiler.AST;

import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

public interface Pass {
    public void runOn(Program program)
	throws ACCError;
}