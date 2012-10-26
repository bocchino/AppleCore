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

    /**
     * Generic visitor method
     */    
    public abstract void accept(Visitor v);

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
     * 6502 BRK
     */
    public static class BRKInstruction
	extends Instruction
    {
	public BRKInstruction() {
	    super("BRK");
	}

	public void accept(Visitor v) {
	    v.visitBRKInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitBRFInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitBRUInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitCFDInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitCFIInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitADDInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitANLInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitDCRInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitDSPInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitICRInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitISPInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitMTVInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitMTSInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitNEGInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitNOTInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitORLInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitORXInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitPHCInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitPVAInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitRAFInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitSHLInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitVTMInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitSTMInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitSUBInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitTEQInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitDIVInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitEXTInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitMULInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitSHRInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitTGEInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitTGTInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitTLEInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitTLTInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitLabelInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitCommentInstruction(this);
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

	public void accept(Visitor v) {
	    v.visitNativeInstruction(this);
	}

	public String toString() {
	    return instructionString(operand);
	}
    }

    /**
     * Generic visitor interface.
     */
    public interface Visitor {

	public void visitBRKInstruction(BRKInstruction inst);
	public void visitBRFInstruction(BRFInstruction inst);
	public void visitBRUInstruction(BRUInstruction inst);
	public void visitCFDInstruction(CFDInstruction inst);
	public void visitCFIInstruction(CFIInstruction inst);
	public void visitADDInstruction(ADDInstruction inst);
	public void visitANLInstruction(ANLInstruction inst);
	public void visitDCRInstruction(DCRInstruction inst);
	public void visitDSPInstruction(DSPInstruction inst);
	public void visitICRInstruction(ICRInstruction inst);
	public void visitISPInstruction(ISPInstruction inst);
	public void visitMTSInstruction(MTSInstruction inst);
	public void visitMTVInstruction(MTVInstruction inst);
	public void visitNEGInstruction(NEGInstruction inst);
	public void visitNOTInstruction(NOTInstruction inst);
	public void visitORLInstruction(ORLInstruction inst);
	public void visitORXInstruction(ORXInstruction inst);
	public void visitPHCInstruction(PHCInstruction inst);
	public void visitPVAInstruction(PVAInstruction inst);
	public void visitRAFInstruction(RAFInstruction inst);
	public void visitSHLInstruction(SHLInstruction inst);
	public void visitSTMInstruction(STMInstruction inst);
	public void visitSUBInstruction(SUBInstruction inst);
	public void visitTEQInstruction(TEQInstruction inst);
	public void visitVTMInstruction(VTMInstruction inst);
	public void visitDIVInstruction(DIVInstruction inst);
	public void visitEXTInstruction(EXTInstruction inst);
	public void visitMULInstruction(MULInstruction inst);
	public void visitSHRInstruction(SHRInstruction inst);
	public void visitTGEInstruction(TGEInstruction inst);
	public void visitTGTInstruction(TGTInstruction inst);
	public void visitTLEInstruction(TLEInstruction inst);
	public void visitTLTInstruction(TLTInstruction inst);
	public void visitLabelInstruction(LabelInstruction inst);
	public void visitCommentInstruction(CommentInstruction inst);
	public void visitNativeInstruction(NativeInstruction inst);

    }


}