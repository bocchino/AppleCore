package AppleCoreCompiler.Compiler;
import java.io.*;
import java.util.*;
import AppleCoreCompiler.Syntax.*;
import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Semantics.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.CodeGen.*;
import AppleCoreCompiler.Warnings.*;

public class Main {

    public static void main(String args[]) {
	Parser parser = null;
	String inputFileName = null;
	try {
	    inputFileName = args[0];
	    System.err.println("Compiling " + inputFileName);
	    parser = new Parser(inputFileName);
	}
	catch (ArrayIndexOutOfBoundsException e) {
	    System.err.println("usage: acc [filename]");
	    System.exit(1);
	}
	System.err.println("...parsing");
	SourceFile sourceFile = parser.parse();
	Warner warner = new Warner(System.err,inputFileName);
	if (sourceFile != null) {
	    try {
		System.err.println("...attributing tree");
		AttributionPass attributionPass = 
		    new AttributionPass(warner);
		attributionPass.runOn(sourceFile);
		System.err.println("...computing expression sizes");
		SizePass sizePass = new SizePass();
		sizePass.runOn(sourceFile);
		System.err.println("...checking lvalues");
		LValuePass lvaluePass = new LValuePass();
		lvaluePass.runOn(sourceFile);
		System.err.println("...checking expression statements");
		ExprStmtPass exprStmtPass = new ExprStmtPass();
		exprStmtPass.runOn(sourceFile);
		System.err.println("...checking function calls");
		FunctionCallPass functionCallPass = new FunctionCallPass();
		functionCallPass.runOn(sourceFile);
		System.err.println("...generating assembly code");
		SCMacroWriter scMacroWriter = 
		    new SCMacroWriter(System.out);
		scMacroWriter.runOn(sourceFile);
		//System.err.println("...printing out the AST");
		//new ASTPrintingPass(System.out).runOn(sourceFile);
	    }
	    catch (ACCError e) {
		System.err.print("line " + e.getLineNumber() + " of " + 
				 inputFileName + ": ");
		System.err.println(e.getMessage());

	    }
	} else { System.exit(1); }
    }

}
