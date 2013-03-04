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
	super(message, node.sourceFileName, node.lineNumber);
    }

    public static SyntaxError expected(String expected,
				       String sourceFileName,
				       Node found) {
	return new SyntaxError("expected " + expected + " but found " +
			       found, sourceFileName,
			       found.lineNumber);
    }

}
