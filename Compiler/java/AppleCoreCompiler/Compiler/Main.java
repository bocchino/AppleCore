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

    /**
     * Name of the source file to parse
     */
    private static String sourceFileName = null;

    /**
     * Whether to translate the source file in include mode
     */
    private static boolean includeMode = false;

    /**
     * Origin of translated assembly file
     */
    private static int origin = -1;

    /**
     * Process command-line arguments
     */
    private static void processArgs(String args[]) 
    {
	try {
	    for (int i = 0; i < args.length; ++i) {
		processArg(args[i]);
	    }
	}
	catch (OptionError e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
	catch (Exception e) {
	    System.err.println("usage: acc [options] [filename]");
	    System.exit(1);
	}
    }

    private static void processArg(String arg) 
	throws OptionError
    {
	if (arg.charAt(0) != '-') {
	    if (sourceFileName == null) {
		sourceFileName = arg;
	    }
	    else {
		throw new OptionError("only one source file allowed");
	    }
	}
	else if (arg.equals("-include")) {
	    includeMode = true;
	}
	else if (arg.substring(0,7).equals("-origin=")) {
	    if (arg.charAt(8)=='$') {
		origin = Integer.parseInt(arg.substring(9),16);
	    }
	    else {
		origin = Integer.parseInt(arg.substring(8));
	    }
	    if (origin < 0) origin = -origin;
	    if (origin > 65535) {
		throw new OptionError("address " + origin + " out of range");
	    }
	}
	else {
	    throw new OptionError("bad option " + arg);
	}
    }

    public static void main(String args[]) {
	processArgs(args);
	Parser parser = new Parser(sourceFileName);
	SourceFile sourceFile = parser.parse();
	sourceFile.includeMode = includeMode;
	sourceFile.origin = origin;
	Warner warner = new Warner(System.err,sourceFileName);
	if (sourceFile != null) {
	    try {
		AttributionPass attributionPass = 
		    new AttributionPass(warner);
		attributionPass.runOn(sourceFile);
		SizePass sizePass = new SizePass();
		sizePass.runOn(sourceFile);
		LValuePass lvaluePass = new LValuePass();
		lvaluePass.runOn(sourceFile);
		ExprStmtPass exprStmtPass = new ExprStmtPass();
		exprStmtPass.runOn(sourceFile);
		FunctionCallPass functionCallPass = new FunctionCallPass();
		functionCallPass.runOn(sourceFile);
		SCMacroWriter scMacroWriter = 
		    new SCMacroWriter(System.out);
		scMacroWriter.runOn(sourceFile);
	    }
	    catch (ACCError e) {
		System.err.print("line " + e.getLineNumber() + " of " + 
				 sourceFileName + ": ");
		System.err.println(e.getMessage());

	    }
	} else { System.exit(1); }
    }

}
