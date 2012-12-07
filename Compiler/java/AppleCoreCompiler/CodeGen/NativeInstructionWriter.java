/**
 * Visitor for translating AVM instructions to native code
 */
package AppleCoreCompiler.CodeGen;

import AppleCoreCompiler.AVM.*;
import AppleCoreCompiler.AVM.Instruction.*;

import java.math.*;

public class NativeInstructionWriter extends InstructionVisitor {

    public final String functionName;
    public final NativeCodeEmitter emitter;

    public NativeInstructionWriter(String functionName,
				   NativeCodeEmitter emitter) {
	this.functionName = functionName;
	this.emitter = emitter;
    }

    int branchLabelCount = 1;
    private String getLabel(String name) {
	return functionName + "." + name + "." + (branchLabelCount++);
    }

    public void visitBRKInstruction(BRKInstruction inst) {
	emitter.emitInstruction("BRK");
    }

    public void visitBRFInstruction(BRFInstruction inst) {
	emitter.emitAbsoluteInstruction("JSR","AVM.POP.A");
	emitter.emitImmediateInstruction("AND",1);
	String label = getLabel("BRF");
	emitter.emitAbsoluteInstruction("BNE",label);
	emitter.emitAbsoluteInstruction("JMP",inst.target.value);
	emitter.emitLabel(label);
	emitter.printStream.println();
    }

    public void visitBRUInstruction(BRUInstruction inst) {
	emitter.emitAbsoluteInstruction("JMP",inst.target.value);
    }

    public void visitCFDInstruction(CFDInstruction inst) {
	emitter.emitAbsoluteInstruction("JSR","MON.IOREST");
	emitter.emitAbsoluteInstruction("JSR",inst.address.toString());
	emitter.emitAbsoluteInstruction("JSR","MON.IOSAVE");
    }

    public void visitCFIInstruction(CFIInstruction inst) {
	emitter.emitAbsoluteInstruction("JSR","AVM.CFI");
    }

    public void visitSizedInstruction(SizedInstruction inst) {
	emitter.emitImmediateInstruction("LDA",inst.size);
	emitter.emitAbsoluteInstruction("JSR","AVM."+inst.mnemonic);
    }

    public void visitMTVInstruction(MTVInstruction inst) {
	emitter.emitImmediateInstruction("LDY",inst.size);
	emitter.emitAbsoluteInstruction("LDA",inst.address.toString());
	emitter.emitIndirectYInstruction("STA","AVM.FP");
    }

    public void visitVTMInstruction(VTMInstruction inst) {
	emitter.emitImmediateInstruction("LDY",inst.size);
	emitter.emitIndirectYInstruction("LDA","AVM.FP");
	emitter.emitAbsoluteInstruction("STA",inst.address.toString());
    }

    private BigInteger byteMask = new BigInteger("FF",16);

    public void visitPHCInstruction(PHCInstruction inst) {
	if (inst.constant != null) {
	    BigInteger bigInt = inst.constant.unsignedValue();
	    do {
		int byteVal = bigInt.and(byteMask).intValue();
		emitter.emitImmediateInstruction("LDA",byteVal);
		emitter.emitAbsoluteInstruction("JSR","AVM.PUSH.A");
		bigInt = bigInt.shiftRight(8);
	    } while (bigInt.compareTo(BigInteger.ZERO) > 0);
	}
	else {
	    String address = inst.address.toString();
	    emitter.emitImmediateInstruction("LDA",address,false);
	    emitter.emitAbsoluteInstruction("JSR","AVM.PUSH.A");
	    emitter.emitImmediateInstruction("LDA",address,true);
	    emitter.emitAbsoluteInstruction("JSR","AVM.PUSH.A");
	}
    }

    public void visitSignedInstruction(SignedInstruction inst) {
	emitter.emitImmediateInstruction("LDX",inst.isSigned ? 1 : 0);
	visitSizedInstruction(inst);
    }

    public void visitNativeInstruction(NativeInstruction inst) {
	// Write out function preamble
	emitter.emitImmediateInstruction("LDA",2);
	emitter.emitAbsoluteInstruction("JSR","AVM.ISP");
	emitter.emitInstruction("PLA");
	emitter.emitAbsoluteInstruction("JSR","AVM.PUSH.A");
	emitter.emitInstruction("PLA");
	emitter.emitAbsoluteInstruction("JSR","AVM.PUSH.A");
	emitter.emitAbsoluteInstruction("JSR","AVM.PUSH.FP");
	emitter.emitAbsoluteInstruction("JSR","AVM.SET.FP.TO.SP");
    }

    public void visitInstruction(Instruction inst) {
	emitter.printStream.println(emitter.makeLabel(inst.toString()));
    }

}