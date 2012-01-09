/**
 * Class representing an AppleCore Virtual Machine instruction.
 */
package AppleCoreCompiler.AVM;

import AppleCoreCompiler.AST.*;
import AppleCoreCompiler.AST.Node.*;

public abstract class Instruction {

    public final String mnemonic;

    public Instruction(String mnemonic) {
	this.mnemonic = mnemonic;
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
	    super("BRK");
	}

    }

    /**
     * 6502 BRK
     */
    public static class BRKInstruction
	extends Instruction
    {
	public BRKInstruction() {
	    super("BRK");
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
	    super("BRF");
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
	    super("BRU");
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
	    super("CFD");
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
	    super("CFI");
	}

    }

    /**
     * Abstract class representing instructions with a size argument
     */
    public abstract static class SizedInstruction 
	extends Instruction
    {
	public final int size;

	protected SizedInstruction(String mnemonic, int size) {
	    super(mnemonic);
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
	    super("ADD",size);
	}
    }

    /**
     * And Logical
     */
    public static class ANLInstruction
	extends SizedInstruction
    {
	public ANLInstruction(int size) {
	    super("ANL",size);
	}
    }

    /**
     * Decrement Variable on Stack
     */
    public static class DCRInstruction
	extends SizedInstruction
    {
	public DCRInstruction(int size) {
	    super("DCR",size);
	}
    }

    /**
     * Decrease Stack Pointer
     */
    public static class DSPInstruction
	extends SizedInstruction
    {
	public DSPInstruction(int size) {
	    super("DSP",size);
	}
    }

    /**
     * Increment Variable on Stack
     */
    public static class ICRInstruction
	extends SizedInstruction
    {
	public ICRInstruction(int size) {
	    super("ICR",size);
	}
    }

    /**
     * Increase Stack Pointer
     */
    public static class ISPInstruction
	extends SizedInstruction
    {
	public ISPInstruction(int size) {
	    super("ISP",size);
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
	    super("MTV",offset);
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
	    super("MTS",size);
	}
    }

    /**
     * Arithmetic Negation
     */
    public static class NEGInstruction
	extends SizedInstruction
    {
	public NEGInstruction(int size) {
	    super("NEG",size);
	}
    }

    /**
     * Logical Not
     */
    public static class NOTInstruction
	extends SizedInstruction
    {
	public NOTInstruction(int size) {
	    super("NOT",size);
	}
    }
	
    /**
     * Or Logical
     */
    public static class ORLInstruction
	extends SizedInstruction
    {
	public ORLInstruction(int size) {
	    super("ORL",size);
	}
    }

    /**
     * Or Exclusive
     */
    public static class ORXInstruction
	extends SizedInstruction
    {
	public ORXInstruction(int size) {
	    super("ORX",size);
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
	    super("PHC",constant.getSize());
	    this.constant = constant;
	    this.address = null;
	}

	public PHCInstruction(Address address) {
	    super("PHC",2);
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
	    super("PVA", slot);
	}
    }

    /**
     * Return from AppleCore Function
     */
    public static class RAFInstruction
	extends SizedInstruction
    {
	public RAFInstruction(int size) {
	    super("RAF",size);
	}
    }

    /**
     * Shift Left
     */
    public static class SHLInstruction
	extends SizedInstruction
    {
	public SHLInstruction(int size) {
	    super("SHL",size);
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
	    super("VTM",offset);
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
	    super("STM",size);
	}
    }

    /**
     * Subtract
     */
    public static class SUBInstruction
	extends SizedInstruction
    {
	public SUBInstruction(int size) {
	    super("SUB",size);
	}
    }

    /**
     * Test Equal
     */
    public static class TEQInstruction
	extends SizedInstruction
    {
	public TEQInstruction(int size) {
	    super("TEQ",size);
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

	protected SignedInstruction(String mnemonic, 
				    int size, boolean isSigned) {
	    super(mnemonic, size);
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
	    super("DIV",size,isSigned);
	}
    }

    /**
     * Extend
     */
    public static class EXTInstruction
	extends SignedInstruction
    {
	public EXTInstruction(int size, boolean isSigned) {
	    super("EXT",size,isSigned);
	}
    }
    
    /**
     * Multiply
     */
    public static class MULInstruction
	extends SignedInstruction
    {
	public MULInstruction(int size, boolean isSigned) {
	    super("MUL",size,isSigned);
	}
    }

    /**
     * Shift Right
     */
    public static class SHRInstruction
	extends SignedInstruction
    {
	public SHRInstruction(int size, boolean isSigned) {
	    super("SHR",size,isSigned);
	}
    }

    /**
     * Test Greater or Equal
     */
    public static class TGEInstruction
	extends SignedInstruction
    {
	public TGEInstruction(int size, boolean isSigned) {
	    super("TGE",size,isSigned);
	}
    }

    /**
     * Test Greater Than
     */
    public static class TGTInstruction
	extends SignedInstruction
    {
	public TGTInstruction(int size, boolean isSigned) {
	    super("TGT",size,isSigned);
	}
    }

    /**
     * Test Less or Equal
     */
    public static class TLEInstruction
	extends SignedInstruction
    {
	public TLEInstruction(int size, boolean isSigned) {
	    super("TLE",size,isSigned);
	}
    }

    /**
     * Test Less Than
     */
    public static class TLTInstruction
	extends SignedInstruction
    {
	public TLTInstruction(int size, boolean isSigned) {
	    super("TLT",size,isSigned);
	}
    }

    /**
     * Pseudoinstructions for use during translation
     */
    public static abstract class Pseudoinstruction
	extends Instruction
    {
	public Pseudoinstruction() {
	    super(null);
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

    /**
     * 6502 instructions
     */
    public static class NativeInstruction 
	extends Instruction
    {

	public final String operand;
	public NativeInstruction(String mnemonic,
				 String operand) {
	    super(mnemonic);
	    this.operand=operand;
	}
	public String toString() {
	    return instructionString(operand);
	}
    }


}