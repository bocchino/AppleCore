/**
 * This class encapsulates the function of printing out warnings.
 */
package AppleCoreCompiler.Warnings;

import AppleCoreCompiler.AST.*;
import java.io.*;

public class Warner {

    private final PrintStream printStream;
    private final String fileName;

    public Warner(PrintStream printStream, String fileName) {
	this.printStream = printStream;
	this.fileName = fileName;
    }

    public void warn(String msg, Node node) {
	printStream.println("warning: line " + node.lineNumber + " of " +
			    fileName + ": " + msg);
    }
}
