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
     * Whether to translate AVM instructions to native code
     */
    private static boolean nativeMode = false;

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
	    if (sourceFileName==null) {
		throw new NullPointerException();
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
	else if (arg.startsWith("-decls=")) {
	    for (String declFile : arg.substring(7).split(":")) {
		declFiles.add(declFile);
	    }
	}
	else if (arg.equals("-include")) {
	    includeMode = true;
	}
	else if (arg.equals("-native")) {
	    nativeMode = true;
	}
	else if (arg.startsWith("-tf=")) {
	    targetFileName = arg.substring(4);
	}
	else if (arg.startsWith("-origin=")) {
	    if (arg.charAt(8)=='$') {
		origin = Integer.parseInt(arg.substring(9),16);
	    }
	    else {
		origin = Integer.parseInt(arg.substring(8));
	    }
	    if (origin < 0) origin = -origin;
	    if (origin > 65535) {
		throw new OptionError("address " + origin + 
				      " out of range");
	    }
	}
	else {
	    throw new OptionError("acc: bad option " + arg);
	}
    }

    public static void main(String args[]) {
	processArgs(args);
	try {
	    Parser parser = new Parser(sourceFileName);
	    SourceFile sourceFile = parser.parse();
	    sourceFile.includeMode = includeMode;
	    sourceFile.nativeMode = nativeMode;
	    sourceFile.origin = origin;
	    sourceFile.targetFile = (targetFileName == null) ?
		defaultTargetFile(sourceFile.name) :
		targetFileName;

	    importDecls(sourceFile);
	    
	    AttributionPass attributionPass = 
		new AttributionPass(declFiles);
	    attributionPass.runOn(sourceFile);
	    
	    FunctionCallPass functionCallPass = new FunctionCallPass();
	    functionCallPass.runOn(sourceFile);
	    
	    ConstantEvaluationPass cePass = 
		new ConstantEvaluationPass();
	    cePass.runOn(sourceFile);

	    GlobalExpressionPass globalExpressionPass =
		new GlobalExpressionPass();
	    globalExpressionPass.runOn(sourceFile);
	    
	    SizePass sizePass = new SizePass();
	    sizePass.runOn(sourceFile);
	    
	    LValuePass lvaluePass = new LValuePass();
	    lvaluePass.runOn(sourceFile);
	    
	    AVMTranslatorPass translatorPass =
		new AVMTranslatorPass();
	    translatorPass.runOn(sourceFile);
	    
	    SourceFileWriter writer =
		new SourceFileWriter(new SCMacroEmitter(System.out));
	    writer.runOn(sourceFile);
	}
	catch (ACCError e) {
	    e.show();
	    System.exit(1);
	}
	catch (FileNotFoundException e) {
	    System.err.println("acc: file " + sourceFileName + " not found");
	    System.exit(1);
	}
	catch (IOException e) {
	    System.err.println("acc: I/O exception");
	    System.exit(1);
	}
    }

    private static void importDecls(SourceFile sourceFile) 
	throws ACCError, IOException
    {
	// Import decls for built-in functions
	String appleCore = System.getenv("APPLECORE");
	if (appleCore == null) {
	    throw new SemanticError("environment variable APPLECORE not set");
	}
	String builtInFns = appleCore + 
	    "/Compiler/java/AppleCoreCompiler/Semantics/BUILT.IN.FNS.ac";
	importDeclsFrom(builtInFns, sourceFile);
	// Import decls specified on command line
	for (String declFile : declFiles) {
	    if (!declFile.equals(sourceFileName)) {
		importDeclsFrom(declFile, sourceFile);
	    }
	}
    }

    private static void importDeclsFrom(String importFileName,
					SourceFile sourceFile) 
	throws ACCError, IOException
    {
	Parser parser = new Parser(importFileName);
	SourceFile importFile = parser.parse();
	for (Declaration decl : importFile.decls) {
	    if (!(decl instanceof IncludeDecl)) {
		decl.isExternal = true;
		sourceFile.importedDecls.add(decl);
	    }
	}
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
