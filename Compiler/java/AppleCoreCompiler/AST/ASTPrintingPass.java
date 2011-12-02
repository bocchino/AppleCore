/**
 * Scan the AST and dump it out in readable form
 */
package AppleCoreCompiler.AST;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

import java.util.*;
import java.io.*;

public class ASTPrintingPass
    extends ASTScanner 
    implements Pass
{
    private final PrintStream printStream;
    private int indentPos = 0;
    private final int indentAmount = 2;
    private void indent() {
	indentPos += indentAmount;
    }
    private void unindent() {
	indentPos -= indentAmount;
    }
    private void printIndented(String s) {
	for (int i = 0; i < indentPos; ++i) {
	    printStream.print(" ");
	}
	printStream.print(s);
    }
    private void newline() {
	printStream.println();
    }

    public ASTPrintingPass(PrintStream printStream) {
	this.printStream = printStream;
    }

    public void runOn(Program program) 
	throws ACCError
    {
	indentPos = 0;
	scan(program);
    }

    public void visitBeforeScan(Node node) 
	throws ACCError
    {
	printStream.format("%04d: ",node.lineNumber);
	printIndented(node.toString());
	newline();
	indent();
    }

    public void visitAfterScan(Node node)
	throws ACCError
    {
	unindent();
    }
}