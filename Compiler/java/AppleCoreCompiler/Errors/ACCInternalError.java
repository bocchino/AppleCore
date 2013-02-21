/**
 * This class represents a semantic error
 */
package AppleCoreCompiler.Errors;

import AppleCoreCompiler.AST.*;

public class ACCInternalError extends ACCError {

    public ACCInternalError() {
	super("internal compiler error");
    }

    public ACCInternalError(String message) {
	super("internal compiler error: " + message);
    }

    public ACCInternalError(String message, Node node) {
	super("internal compiler error: " + message, 
	      node.lineNumber);
    }

}
