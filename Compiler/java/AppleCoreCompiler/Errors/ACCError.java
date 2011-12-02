/**
 * This class represents a generic error.
 */
package AppleCoreCompiler.Errors;

public abstract class ACCError extends Exception {

    // CONSTRUCTORS

    public ACCError(String message) {
	super(message);
    }

    public ACCError(String message, int lineNumber) {
	super(message);
	this.lineNumber = lineNumber;
    }

    // INSTANCE METHODS

    /**
     * Returns the line number of the exception
     */
    public int getLineNumber() {
	return lineNumber;
    }

    /**
     * Sets the line number
     */
    public void setLineNumber(int lineNumber) {
	this.lineNumber = lineNumber;
    }

    /**
     * The line number where the exception occurred.
     */
    private int lineNumber;

}
