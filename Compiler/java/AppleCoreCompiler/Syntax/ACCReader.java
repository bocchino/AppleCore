/**
 * This class provides a pushback reader that keeps track of the
 * source line of each character read from the stream.
 */
package AppleCoreCompiler.Syntax;

import java.io.*;

public class ACCReader extends PushbackReader {

    public ACCReader(Reader in) {
	super(in);
    }

    /**
     * Stores the source line of the next character to be read.
     */
    private int lineNumber = 1;

    /**
     * Reads a character, incrementing the line if it is a newline
     * character.
     */
    public int read() throws IOException {
	int ch = super.read();
	if(ch == '\n') ++lineNumber;
	return ch;
    }

    /**
     * Pushes a character on the stream, decrementing the line if it
     * is a newline character.
     */
    public void unread(int c) throws IOException {
	if (c == '\n') --lineNumber;
	if (c >= 0) super.unread(c);
    }

    /**
     * Gets the character number
     */

    public int getLineNumber() {
	return lineNumber;
    }
}
