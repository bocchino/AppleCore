package AppleCoreCompiler.Compiler;
import java.io.*;
import java.util.*;
import AppleCoreCompiler.Syntax.*;
import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Transforms.*;
import AppleCoreCompiler.Semantics.*;
import AppleCoreCompiler.Errors.*;
import AppleCoreCompiler.CodeGen.*;
import AppleCoreCompiler.Warnings.*;
import AppleCoreCompiler.AVM.*;

public class Main {

    /**
     * Name of the source file to parse
     */
    private static String sourceFileName = null;

    /**
     * Name of the target file
     */
    private static String targetFileName = null;
    
    /**
     * Whether to translate the source file in include mode
     */
    private static boolean includeMode = false;

    /**
     * Whether to print verbose comments in generated code
     */
    private static boolean printVerboseComments = false;

    /**
     * Whether to generate AppleCore Virtual Machine code;
     */
    private static boolean generateAVMCode = false;

    /**
     * Origin of translated assembly file
     */
    private static int origin = -1;

    /**
     * List of file names from which to extract decls
     */
    private static List<String> declFiles = 
	new LinkedList<String>();

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
	    System.err.println("usage: acc [options] filename");
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
	else if (arg.equals("-avm")) {
	    generateAVMCode = true;
	}
	else if (arg.substring(0,7).equals("-decls=")) {
	    for (String declFile : arg.substring(7).split(":")) {
		declFiles.add(declFile);
	    }
	}
	else if (arg.equals("-verbose")) {
	    printVerboseComments=true;
	}
	else if (arg.equals("-include")) {
	    includeMode = true;
	}
	else if (arg.substring(0,4).equals("-tf=")) {
	    targetFileName = arg.substring(4);
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
	if (sourceFile != null) {
	    try {
		sourceFile.includeMode = includeMode;
		sourceFile.origin = origin;
		sourceFile.targetFile = (targetFileName == null) ?
		    defaultTargetFile(sourceFile.name) :
		    targetFileName;
		AttributionPass attributionPass = 
		    new AttributionPass(declFiles);
		attributionPass.runOn(sourceFile);
		ConstantEvaluationPass cePass = 
		    new ConstantEvaluationPass();
		cePass.runOn(sourceFile);
		/*
		ASTPrintingPass app = new ASTPrintingPass(System.err);
		app.runOn(sourceFile);
		*/
		SizePass sizePass = new SizePass();
		sizePass.runOn(sourceFile);
		LValuePass lvaluePass = new LValuePass();
		lvaluePass.runOn(sourceFile);
		ExprStmtPass exprStmtPass = new ExprStmtPass();
		exprStmtPass.runOn(sourceFile);
		FunctionCallPass functionCallPass = new FunctionCallPass();
		functionCallPass.runOn(sourceFile);

		AVMTranslatorPass translatorPass =
		    new AVMTranslatorPass();
		translatorPass.runOn(sourceFile);

		SourceFileWriter.Mode mode =
		    generateAVMCode ? SourceFileWriter.Mode.AVM :
		    SourceFileWriter.Mode.NATIVE;
		SourceFileWriter writer =
		    new SourceFileWriter(new SCMacroEmitter(System.out),
					 mode);
		writer.printVerboseComments =
		    printVerboseComments;
		writer.runOn(sourceFile);
	    }
	    catch (ACCError e) {
		System.err.print("line " + e.getLineNumber() + " of " + 
				 sourceFileName + ": ");
		System.err.println(e.getMessage());

	    }
	} else { System.exit(1); }
    }

    private static String defaultTargetFile(String sourceFile) {
	String[] pathNames = sourceFile.split("/");
	String targetFile = pathNames[pathNames.length-1];
	targetFile = targetFile.replace('_','.').toUpperCase();
	if (targetFile.length() > 3 &&
	    targetFile.substring(targetFile.length()-3).equals(".AC")) {
	    targetFile = targetFile.substring(0,targetFile.length()-3);
	}
	targetFile = targetFile + ".OBJ";
	return targetFile;
    }

}
