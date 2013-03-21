/**
 * This class represents a file not found error.
 */
package AppleCoreCompiler.Errors;

public class FileNotFoundError extends ACCError {

    public FileNotFoundError(String fileName) {
	super("file " + fileName + " not found");
    }

    public void show() {
	System.err.print("acc: ");
	System.err.println(this.getMessage());
    }

}
