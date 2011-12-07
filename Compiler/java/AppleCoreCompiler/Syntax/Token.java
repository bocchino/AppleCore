/**
 * This class represents a lexical token.
 */
package AppleCoreCompiler.Syntax;

import java.util.*;
import java.math.*;

public enum Token {
    AND(TokenType.KEYWORD,"AND"),
    CONST(TokenType.KEYWORD,"CONST"),
    DATA(TokenType.KEYWORD,"DATA"),
    DECR(TokenType.KEYWORD,"DECR"),
    ELSE(TokenType.KEYWORD,"ELSE"),
    FN(TokenType.KEYWORD,"FN"),
    IF(TokenType.KEYWORD,"IF"),
    INCLUDE(TokenType.KEYWORD,"INCLUDE"),
    INCR(TokenType.KEYWORD,"INCR"),
    NOT(TokenType.KEYWORD,"NOT"),
    OR(TokenType.KEYWORD,"OR"),
    RETURN(TokenType.KEYWORD,"RETURN"),
    SET(TokenType.KEYWORD,"SET"),
    VAR(TokenType.KEYWORD,"VAR"),
    XOR(TokenType.KEYWORD,"XOR"),
    WHILE(TokenType.KEYWORD,"WHILE"),
    AT(TokenType.SYMBOL,"@"),
    CARET(TokenType.SYMBOL,"^"),
    STAR(TokenType.SYMBOL,"*"),
    SLASH(TokenType.SYMBOL,"/"),
    PLUS(TokenType.SYMBOL,"+"),
    NEG(TokenType.SYMBOL,"-"),
    SHL(TokenType.SYMBOL,"<<"),
    SHR(TokenType.SYMBOL,">>"),
    GEQ(TokenType.SYMBOL,">="),
    LEQ(TokenType.SYMBOL,"<="),
    GT(TokenType.SYMBOL,">"),
    LT(TokenType.SYMBOL,"<"),
    BACKSLASH(TokenType.SYMBOL,"\\"),
    LPAREN(TokenType.SYMBOL,"("),
    RPAREN(TokenType.SYMBOL,")"),
    LBRACE(TokenType.SYMBOL,"{"),
    RBRACE(TokenType.SYMBOL,"}"),
    LBRACKET(TokenType.SYMBOL,"["),
    RBRACKET(TokenType.SYMBOL,"]"),
    SEMI(TokenType.SYMBOL,";"),
    COLON(TokenType.SYMBOL,":"),
    EQUALS(TokenType.SYMBOL,"="),
    COMMA(TokenType.SYMBOL,","),
    IDENT(TokenType.IDENT),
    STRING_CONST(TokenType.CONST),
    // TODO: Add backslash as a symbol, get rid of UNTERMINATED_STRING_CONSTANT
    UNTERMINATED_STRING_CONST(TokenType.CONST),
    CHAR_CONST(TokenType.CONST),
    DEC_CONST(TokenType.CONST),
    HEX_CONST(TokenType.CONST),
    END(TokenType.MARKER,"end of file");

    /**
     * List of keywords
     */
    public static final List<Token> keywords = 
	new LinkedList<Token>();

    /**
     * List of symbols
     */
    public static final List<Token> symbols =
	new LinkedList<Token>();

    static {
	for (Token token : values()) {
	    switch (token.type) {
	    case KEYWORD:
		keywords.add(token);
		break;
	    case SYMBOL:
		symbols.add(token);
		break;
	    }
	}
    }

    /**
     * The type of the token
     */
    public TokenType type;

    /**
     * The string value of the token
     */
    public String stringValue;

    /**
     * The numeric value of the token
     */
    public BigInteger numberValue;

    /**
     * The source line where the token occurred
     */
    public int lineNumber;

    Token(TokenType type) {
	this.type = type;
    }

    Token(TokenType type, String stringValue) {
	this(type);
	this.stringValue = stringValue;
    }

    public void setLineNumber(int lineNumber) {
	this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
	return lineNumber;
    }

    public void setStringValue(String stringValue) {
	this.stringValue = stringValue;
    }

    public String getStringValue() {
	return this.stringValue;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	if (this == STRING_CONST) {
	    sb.append("\"");
	    sb.append(stringValue);
	    sb.append("\"");
	}
	else if (this == CHAR_CONST) {
	    sb.append("'");
	    sb.append(stringValue);
	    sb.append("'");
	}
	else sb.append(stringValue);
	return sb.toString();
    }

    public void setNumberValue(BigInteger numberValue) {
	this.numberValue = numberValue;
    }

    public BigInteger getNumberValue() {
	return numberValue;
    }

    public String expectedString() {
	switch (type) {
	case IDENT:
	case CONST:
	    return type.toString();
	default:
	    return this.toString();
	}	    
    }
}
