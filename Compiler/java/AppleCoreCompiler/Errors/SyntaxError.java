/**
 * This class represents a syntax error, i.e., an error in scanning or
 * parsing.
 */
package AppleCoreCompiler.Errors;

import AppleCoreCompiler.Syntax.*;

public class SyntaxError extends ACCError {

    public SyntaxError(String message) {
	super(message);
    }

    public SyntaxError(String message, int lineNumber) {
	super(message, lineNumber);
    }

    public static SyntaxError expected(String expected,
				       Token found) {
	return new SyntaxError("expected " + expected + " but found " +
			       found, found.lineNumber);
    }

}
