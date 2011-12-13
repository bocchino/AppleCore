/**
 * This class provides methods for extracting tokens
 * from a stream of text.
 */
package AppleCoreCompiler.Syntax;

import AppleCoreCompiler.Errors.*;
import java.util.*;
import java.io.*;
import java.math.*;

public class Scanner {

    /**
     * Main method for running the scanner as a stand-alone program.
     */
    public static void main (String[] args) {
        try {
            System.err.println("Scanning " + args[0] + "...");
            FileReader fr = new FileReader(args[0]);
            Scanner scanner = new Scanner(new BufferedReader(fr));
            Token token;
            do {
                token = scanner.getNextToken();
		System.err.println("line " + token.getLineNumber() + ": " + token);
            } while (token != Token.END);
            fr.close();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("usage: java ScannerDriver [filename]");
        }
        catch (SyntaxError e) {
            System.err.print("line " + e.getLineNumber() + ": ");
            System.err.println(e.toString());
        }
        catch (FileNotFoundException e) {
            System.err.println("file " + args[0] + " not found");
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("I/O exception occurred");
            System.exit(1);
        }
    }

    /**
     * Reader for reading characters; remembers the position of the
     * last character read
     */
    private ACCReader reader;

    /**
     * Stores the current token scanned
     */
    private Token currentToken;

    /**
     * Constructs a new Scanner object that stores the input stream to
     * be scanned
     */
    public Scanner(Reader input) {
	reader = new ACCReader(input);
    }

    /**
     * Gets the current token from the token buffer
     */
    public Token getCurrentToken() {
	return currentToken;
    }

    /**
     * Gets the next token from the input stream
     * and stores it in the token buffer
     */
    public Token getNextToken()
	throws SyntaxError, IOException 
    {

	int ch = 0;

	// Skip whitespace and comments
	do  {
	    while(Character.isWhitespace((char) (ch = reader.read()))) {
	    }

	    if (ch == '#') {
		do {
		    ch = reader.read();
		} while (ch >= 0 && ch != '\n');
	    }
	} while (ch == '\n'); 

	// Check for end of file
	if (ch < 0) {
	    currentToken = Token.END;
	    currentToken.setLineNumber(reader.getLineNumber());
	    return currentToken;
	}

	// Check for identifier or keyword
	if (Character.isLetter((char) ch)) {
	    getIdentOrKeyword(ch);
	    return currentToken;
	}

	// Check for decimal constant
	if (Character.isDigit((char) ch)) {
	    getDecimalConstant(ch);
	    return currentToken;
	}

	// Check for hex constant
	if (ch == '$') {
	    getHexConstant();
	    return currentToken;
	}

	// Check for char constant
	if (ch == '\'') {
	    getCharConstant();
	    return currentToken;
	}

	// Check for string constant
	if (ch == '"') {
	    getStringConstant();
	    return currentToken;
	}

	// Check for symbol token
	getSymbol(ch);
	return currentToken;
    }

    /**
     * Gets an identifier or keyword token
     */
    void getIdentOrKeyword(int ch) throws SyntaxError, IOException {
	StringBuffer s = new StringBuffer();
	s.append((char) ch);

	while(Character.isLetter((char) (ch = reader.read())))
	    s.append((char) ch);

	reader.unread(ch);

	String str = s.toString();
	for (Token token : Token.keywords) {
	    if (str.equals(token.getStringValue())) {
		currentToken = token;
		currentToken.setLineNumber(reader.getLineNumber());
		return;
	    }
	}

	do {
	    ch = reader.read();
	    if (!Character.isLetterOrDigit((char)ch) &&
		ch != '_') {
		reader.unread(ch);
		currentToken = Token.IDENT;
		currentToken.setStringValue(s.toString());
		currentToken.setLineNumber(reader.getLineNumber());
		return;
	    }
	    s.append((char)ch);
	} while(true);
    }

    /**
     * Read a decimal number and get a token representing the integer
     * value.
     */
    void getDecimalConstant(int ch) 
	throws IOException, SyntaxError
    {
	StringBuffer s = new StringBuffer();
	s.append((char) ch);

	currentToken = Token.INT_CONST;
	currentToken.setLineNumber(reader.getLineNumber());

	while(Character.isDigit((char) (ch = reader.read())))
	    s.append((char) ch);
	reader.unread(ch);

	BigInteger numberValue = new BigInteger(s.toString());
	checkRange(numberValue);
	currentToken.setNumberValue(numberValue);
	currentToken.setStringValue(s.toString());
    }

    /**
     * Read a hexadecimal number and get a token representing the
     * integer value.
     */
    void getHexConstant() 
	throws IOException, SyntaxError 
    {
	StringBuffer s = new StringBuffer();
	String str = null;

	currentToken = Token.INT_CONST;
	currentToken.setLineNumber(reader.getLineNumber());
	currentToken.wasHexInSource = true;

	int ch = 0;
	while (Character.isLetterOrDigit((char) (ch = reader.read()))) {
	    s.append((char) ch);
	}
	reader.unread(ch);
	
	try {
	    BigInteger numberValue = new BigInteger(s.toString(),16);
	    checkRange(numberValue);
	    currentToken.setStringValue("$"+s.toString());
	    currentToken.setNumberValue(numberValue);
	}
	catch(NumberFormatException e) {
	    throw new SyntaxError("Bad number format $" + str, 
				  currentToken.getLineNumber());
	}
    }

    private void checkRange(BigInteger val) 
	throws SyntaxError
    {
	if (val.compareTo(Token.MAX_INT) > 0) {
	    throw new SyntaxError("integer value out of range",
				  currentToken.getLineNumber());
	}
    }

    public void getStringConstant() 
	throws IOException, SyntaxError 
    {
	StringBuffer s = new StringBuffer();
	int ch = 0;
	while ((ch = readAndCheckForEnd()) != '"') {
	    // Check for escape sequence \$XX
	    if (ch == '\\') {
		char ch1 = readAndCheckForEnd();
		if (ch1 == '$') {
		    s.append(readEscapeSequence());
		}
		else {
		    s.append((char) ch);
		    s.append(ch1);
		}
	    }
	    else {
		s.append((char) ch);
	    }
	}
	currentToken = Token.STRING_CONST;
	currentToken.setLineNumber(reader.getLineNumber());
	currentToken.setStringValue(s.toString());
    }

    private char readEscapeSequence() 
	throws IOException, SyntaxError
    {
	StringBuffer sb = new StringBuffer();
	sb.append(readAndCheckForEnd());
	sb.append(readAndCheckForEnd());
	String str = sb.toString();
	try {
	    int ascii = Integer.valueOf(str,16);
	    if (ascii < 0) {
		throw new NumberFormatException();
	    }
	    return (char) ascii;
	}
	catch(NumberFormatException e) {
	    throw new SyntaxError("Bad number format $" + str, 
				  reader.getLineNumber());
	}
    }

    private char readAndCheckForEnd() 
	throws IOException, SyntaxError
    {
	int ch = reader.read();
	checkForEnd(ch);
	return (char) ch;
    }

    private void checkForEnd(int ch) 
	throws SyntaxError
    {
	if (ch == -1)
	    throw new SyntaxError("Unterminated string",
				  reader.getLineNumber());
    }

    public void getCharConstant()
	throws IOException, SyntaxError
    {
	currentToken = Token.CHAR_CONST;
	currentToken.setLineNumber(reader.getLineNumber());
	int ch1 = reader.read();
	int ch2 = reader.read();
	if (ch1 < 0 || ch2 != '\'') {
	    throw new SyntaxError("Unterminated char literal",
				  currentToken.getLineNumber());
	}
	currentToken.setStringValue(Character.toString((char)ch1));
    }

    public void getSymbol(int ch) 
	throws SyntaxError, IOException 
    {
	currentToken = null;
	for (Token token : Token.symbols) {
	    String s = token.getStringValue();
	    if (s != null) {
		if (ch == s.charAt(0)) {
		    if (s.length() == 1) {
			currentToken = token;
			break;
		    } else {
			int ch1 = reader.read();
			if (ch1 == s.charAt(1)) {
			    currentToken = token;
			    break;
			}
			reader.unread((char) ch1);
		    }
		}
	    }
	}
	if (currentToken != null) {
	    currentToken.setLineNumber(reader.getLineNumber());
	} else {
	    currentToken = Token.END;
	    currentToken.setLineNumber(reader.getLineNumber());
	    throw new SyntaxError("Invalid character \""
				  + (char) ch + "\"", 
				  currentToken.getLineNumber());
	}
    }

    /**
     * Gets the line of the the last character read from the reader
     */
    
    public int getLineNumber() {
	return reader.getLineNumber();
    }

}

