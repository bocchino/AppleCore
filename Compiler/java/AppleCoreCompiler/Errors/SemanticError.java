/**
 * This class represents a semantic error
 */
package AppleCoreCompiler.Errors;

import AppleCoreCompiler.AST.*;

public class SemanticError extends ACCError {

    public SemanticError(String message) {
	super(message);
    }

    public SemanticError(String message, Node node) {
	super(message, node.lineNumber);
    }

    public static SyntaxError expected(String expected,
				       Node found) {
	return new SyntaxError("expected " + expected + " but found " +
			       found, found.lineNumber);
    }

}
