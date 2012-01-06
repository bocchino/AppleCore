/**
 * Class representing an AppleCore Virtual Machine instruction.
 */
package AppleCoreCompiler.AVM;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;

public abstract class Instruction {

    public final String mnemonic;
    public final int opcode;

    public static final int NOP  = 0xEA;

    public static final int BRK  = 0;
    public static final int BRF  = 1;
    public static final int BRU  = 2;
    public static final int CFD  = 4;
    public static final int CFI  = 5;

    public static final int[] unsizedOpcodes = {
	BRK,BRF,BRU,CFD,CFI
    };
    public static final String[] unsizedMnemonics = {
	"BRK","BRF","BRU","CFD","CFI"
    };

    public static final int ADD  = 1<<3;
    public static final int AND  = 2<<3;
    public static final int DEC  = 3<<3;
    public static final int DSP  = 4<<3;
    public static final int INC  = 5<<3;
    public static final int ISP  = 6<<3;
    public static final int MTS  = 7<<3;
    public static final int MTV  = 8<<3;
    public static final int NEG  = 9<<3;
    public static final int NOT  =10<<3;
    public static final int ORL  =11<<3;
    public static final int ORX  =12<<3;
    public static final int PHC  =13<<3;
    public static final int PVA  =14<<3;
    public static final int RAF  =15<<3;
    public static final int SHL  =16<<3;
    public static final int STM  =17<<3;
    public static final int SUB  =18<<3;
    public static final int TEQ  =19<<3;
    public static final int VTM  =20<<3;

    public static final int[] sizedOpcodes = {
	ADD,AND,DEC,DSP,INC,ISP,MTS,MTV,NEG,
	NOT,ORL,ORX,PHC,PVA,RAF,SHL,STM,
	SUB,TEQ,VTM
    };
    public static final String[] sizedMnemonics = {
	"ADD","AND","DEC","DSP","EXT","INC","ISP",
	"MTS","MTV","NEG","NOT","ORL","ORX","PHC","PVA",
	"RAF","SHL","STM","SUB","TEQ","VTM"
    };

    public static final int DIV  =21<<3;
    public static final int EXT  =22<<3;
    public static final int MUL  =23<<3;
    public static final int SHR  =24<<3;
    public static final int TGE  =25<<3;
    public static final int TGT  =26<<3;
    public static final int TLE  =27<<3;
    public static final int TLT  =28<<3;

    public static final int SIGNED=1<<2;

    public static final int[] signedOpcodes = {
	DIV,EXT,MUL,SHR,TGE,TGT,TLE,TLT
    };
    public static final String[] signedMnemonics = {
	"DIV","EXT","MUL","SHR","TGE","TGT","TLE","TLT"
    };

    public static void main(String[] args) {
	for (int i = 0; i < 0x100; ++i) {
	    System.out.print(Address.asHexString(i));
	    System.out.print("\t");
	    System.out.println(decode(i));
	}
    }

    public static final String decode(int opcode) {
	opcode = opcode & 0xFF;
	if (opcode < 6) {
	    return unsizedMnemonics[opcode];
	}
	else if (opcode == 0xEA) {
	    return "NOP";
	}
	else {
	    int selector = (opcode>>3);
	    if (selector >= 1 && selector <= 20) {
		int size = opcode & 7;
		StringBuffer sb = new StringBuffer(sizedMnemonics[selector-1]);
		if (size < 7) {
		    sb.append(" ");
		    sb.append(String.valueOf(size));
		}
		return sb.toString();
	    }
	    else if (selector >= 21 && selector <= 28) {
		boolean signed = (opcode & SIGNED) != 0;
		int size = opcode & 3;
		StringBuffer sb = 
		    new StringBuffer(signedMnemonics[selector-21]);
		sb.append(" ");
		if (size < 3) {
		    sb.append(String.valueOf(size));
		}
		if (signed) {
		    sb.append("S");
		}
		return sb.toString();
	    }
	    else {
		return "???";
	    }
	}
    }

    public Instruction(String mnemonic, int opcode) {
	this.mnemonic = mnemonic;
	this.opcode = opcode;
    }

    protected String instructionString(Object suffix) {
	return mnemonic + " " + suffix.toString();
    }

    protected String instructionString(int value, boolean isSigned) {
	if (!isSigned) {
	    return mnemonic + " " + Address.asHexString(value);
	}
	return mnemonic + " " + Address.asHexString(value) + "S"; 
    }

    protected String instructionString(int value) {
	return instructionString(value, false);
    }

    public String toString() { return mnemonic; }

    /**
     * 6502 NOP
     */
    public static class NOPInstruction
	extends Instruction
    {
	public NOPInstruction() {
	    super("BRK",0xEA);
	}

    }

    /**
     * 6502 BRK
     */
    public static class BRKInstruction
	extends Instruction
    {
	public BRKInstruction() {
	    super("BRK",0);
	}

    }

    /**
     * Branch on Result False
     */
    public static class BRFInstruction
	extends Instruction
    {
	public final LabelInstruction target;

	public BRFInstruction(LabelInstruction target) {
	    super("BRF",BRF);
	    this.target=target;
	}

	public String toString() {
	    return instructionString(target);
	}
    }
    
    /**
     * Branch Unconditionally
     */
    public static class BRUInstruction
	extends Instruction
    {
	public final LabelInstruction target;

	public BRUInstruction(LabelInstruction target) {
	    super("BRU",BRU);
	    this.target=target;
	}

	public String toString() {
	    return instructionString(target);
	}
    }
    
    /**
     * Call Function Direct
     */
    public static class CFDInstruction
	extends Instruction
    {
	public final Address address;

	public CFDInstruction(Address address) {
	    super("CFD",CFD);
	    this.address=address;
	}

	public String toString() {
	    return instructionString(address);
	}
    }

    /**
     * Call Function Indirect
     */
    public static class CFIInstruction
	extends Instruction
    {
	public CFIInstruction() {
	    super("CFI",CFI);
	}

    }

    /**
     * Abstract class representing instructions with a size argument
     */
    public abstract static class SizedInstruction 
	extends Instruction
    {
	public final int size;

	protected SizedInstruction(String mnemonic, int opcode, 
				   int size) {
	    super(mnemonic, sizedOpcode(opcode,size));
	    this.size=size;
	}

	public String toString() {
	    return instructionString(size);
	}
    }

    /**
     * Add
     */
    public static class ADDInstruction
	extends SizedInstruction
    {
	public ADDInstruction(int size) {
	    super("ADD",ADD,size);
	}
    }

    /**
     * And
     */
    public static class ANDInstruction
	extends SizedInstruction
    {
	public ANDInstruction(int size) {
	    super("AND",AND,size);
	}
    }

    /**
     * Decrement Variable on Stack
     */
    public static class DECInstruction
	extends SizedInstruction
    {
	public DECInstruction(int size) {
	    super("DEC",DEC,size);
	}
    }

    /**
     * Decrease Stack Pointer
     */
    public static class DSPInstruction
	extends SizedInstruction
    {
	public DSPInstruction(int size) {
	    super("DSP",DSP,size);
	}
    }

    /**
     * Increment Variable on Stack
     */
    public static class INCInstruction
	extends SizedInstruction
    {
	public INCInstruction(int size) {
	    super("INC",INC,size);
	}
    }

    /**
     * Increase Stack Pointer
     */
    public static class ISPInstruction
	extends SizedInstruction
    {
	public ISPInstruction(int size) {
	    super("ISP",ISP,size);
	}
    }

    /**
     * Memory to Variable
     */
    public static class MTVInstruction
	extends SizedInstruction
    {
	public final Address address;
	public MTVInstruction(int offset, Address address) {
	    super("MTV",MTV,offset);
	    this.address = address;
	}

	public String toString() {
	    return instructionString(Address.asHexString(size) +
				     "<-" + address.toString());
	}
    }
    
    /**
     * Memory To Stack
     */
    public static class MTSInstruction
	extends SizedInstruction
    {
	public MTSInstruction(int size) {
	    super("MTS",MTS,size);
	}
    }

    /**
     * Arithmetic Negation
     */
    public static class NEGInstruction
	extends SizedInstruction
    {
	public NEGInstruction(int size) {
	    super("NEG",NEG,size);
	}
    }

    /**
     * Logical Not
     */
    public static class NOTInstruction
	extends SizedInstruction
    {
	public NOTInstruction(int size) {
	    super("NOT",NOT,size);
	}
    }
	
    /**
     * Or Logical
     */
    public static class ORLInstruction
	extends SizedInstruction
    {
	public ORLInstruction(int size) {
	    super("ORL",ORL,size);
	}
    }

    /**
     * Or Exclusive
     */
    public static class ORXInstruction
	extends SizedInstruction
    {
	public ORXInstruction(int size) {
	    super("ORX",ORX,size);
	}
    }

    /**
     * Push Constant
     */
    public static class PHCInstruction
	extends SizedInstruction
    {
	public final NumericConstant constant;
	public final Address address;

	public PHCInstruction(NumericConstant constant) {
	    super("PHC",PHC,constant.getSize());
	    this.constant = constant;
	    this.address = null;
	}

	public PHCInstruction(Address address) {
	    super("PHC",PHC,2);
	    this.constant = null;
	    this.address = address;
	}

	public String toString() {
	    if (constant != null) {
		return instructionString(constant.valueAsHexString());
	    }
	    return instructionString(address);
	}

    }

    /**
     * Push Variable Address
     */
    public static class PVAInstruction
	extends SizedInstruction
    {
	public PVAInstruction(int slot) {
	    super("PVA", PVA, slot);
	}
    }

    /**
     * Return from AppleCore Function
     */
    public static class RAFInstruction
	extends SizedInstruction
    {
	public RAFInstruction(int size) {
	    super("RAF",RAF,size);
	}
    }

    /**
     * Shift Left
     */
    public static class SHLInstruction
	extends SizedInstruction
    {
	public SHLInstruction(int size) {
	    super("SHL",SHL,size);
	}
    }

    /**
     * Variable To Memory
     */
    public static class VTMInstruction
	extends SizedInstruction
    {
	public final Address address;
	public VTMInstruction(int offset, Address address) {
	    super("VTM",VTM,offset);
	    this.address = address;
	}

	public String toString() {
	    return instructionString(Address.asHexString(size) +
				     "->" + address.toString());
	}
    }
    
    /**
     * Stack to Memory
     */
    public static class STMInstruction
	extends SizedInstruction
    {
	public STMInstruction(int size) {
	    super("STM",STM,size);
	}
    }

    /**
     * Subtract
     */
    public static class SUBInstruction
	extends SizedInstruction
    {
	public SUBInstruction(int size) {
	    super("SUB",SUB,size);
	}
    }

    /**
     * Test Equal
     */
    public static class TEQInstruction
	extends SizedInstruction
    {
	public TEQInstruction(int size) {
	    super("TEQ",TEQ,size);
	}
    }

    /**
     * Abstract class representing instructions with a signed size
     * argument.
     */
    public abstract static class SignedInstruction 
	extends SizedInstruction
    {
	public final boolean isSigned;

	protected SignedInstruction(String mnemonic, int opcode, 
				    int size, boolean isSigned) {
	    super(mnemonic, opcode, size);
	    this.isSigned=isSigned;
	}

	public String toString() {
	    return instructionString(size, isSigned);
	}
    }

    /**
     * Divide
     */
    public static class DIVInstruction
	extends SignedInstruction
    {
	public DIVInstruction(int size, boolean isSigned) {
	    super("DIV",DIV,size,isSigned);
	}
    }

    /**
     * Extend
     */
    public static class EXTInstruction
	extends SignedInstruction
    {
	public EXTInstruction(int size, boolean isSigned) {
	    super("EXT",EXT,size,isSigned);
	}
    }
    
    /**
     * Multiply
     */
    public static class MULInstruction
	extends SignedInstruction
    {
	public MULInstruction(int size, boolean isSigned) {
	    super("MUL",MUL,size,isSigned);
	}
    }

    /**
     * Shift Right
     */
    public static class SHRInstruction
	extends SignedInstruction
    {
	public SHRInstruction(int size, boolean isSigned) {
	    super("SHR",SHR,size,isSigned);
	}
    }

    /**
     * Test Greater or Equal
     */
    public static class TGEInstruction
	extends SignedInstruction
    {
	public TGEInstruction(int size, boolean isSigned) {
	    super("TGE",TGE,size,isSigned);
	}
    }

    /**
     * Test Greater Than
     */
    public static class TGTInstruction
	extends SignedInstruction
    {
	public TGTInstruction(int size, boolean isSigned) {
	    super("TGT",TGT,size,isSigned);
	}
    }

    /**
     * Test Less or Equal
     */
    public static class TLEInstruction
	extends SignedInstruction
    {
	public TLEInstruction(int size, boolean isSigned) {
	    super("TLE",TLE,size,isSigned);
	}
    }

    /**
     * Test Less Than
     */
    public static class TLTInstruction
	extends SignedInstruction
    {
	public TLTInstruction(int size, boolean isSigned) {
	    super("TLT",TLT,size,isSigned);
	}
    }

    /**
     * Pseudoinstructions for use during translation
     */
    public static abstract class Pseudoinstruction
	extends Instruction
    {
	public Pseudoinstruction() {
	    super(null,-1);
	}
    }

    /**
     * Label pseudoinstruction
     */
    public static class LabelInstruction
	extends Pseudoinstruction
    {
	public String value;
	public LabelInstruction(String value) {
	    this.value=value;
	}
	public String toString() {
	    return value;
	}
    }

    /**
     * Comment pseudoinstruction
     */
    public static class CommentInstruction 
	extends Pseudoinstruction
    {
	String value;
	public CommentInstruction(String value) {
	    this.value=value;
	}
	public String toString() {
	    return "* "+value;
	}
    }

    protected static int sizedOpcode(int opcode, int size) {
	int result = opcode;
	if (size < 7) {
	    result = result | size;
	}
	else {
	    result = result | 7;
	}
	return result;
    }

    protected static int signedOpcode(int opcode, int size, 
				     boolean isSigned) {
	int result = opcode;
	if (size < 3) {
	    result = result | size;
	}
	else {
	    result = result | 3;
	}
	if (isSigned) {
	    result = result | SIGNED;
	}
	return result;
    }

}