package AppleCoreCompiler.Syntax;

public enum TokenType {
    KEYWORD("keyword"),
    SYMBOL("symbol"),
    CONST("constant"),
    IDENT("identifier"),
    MARKER("marker");

    private String stringValue;

    private TokenType(String stringValue) {
	this.stringValue = stringValue;
    }

    public String toString() {
	return stringValue;
    }
}