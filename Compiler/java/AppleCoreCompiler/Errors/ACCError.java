/**
 * This class represents a generic error.
 */
package AppleCoreCompiler.Errors;

public abstract class ACCError extends Exception {

    /**
     * The name of the source file that produced the error
     */
    private String sourceFileName;

    /**
     * The line number where the error occurred
     */
    private int lineNumber;

    // CONSTRUCTORS

    public ACCError(String message, String sourceFileName,
		    int lineNumber) {
	super(message);
	this.sourceFileName = sourceFileName;
	this.lineNumber = lineNumber;
    }

    public ACCError(String message) {
	this(message, null, 0);
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
     * Display an error message
     */
    public void show() {
	System.err.print("acc: line " + lineNumber + " of " + 
			 sourceFileName + ": ");
	System.err.println(this.getMessage());
    }

}
