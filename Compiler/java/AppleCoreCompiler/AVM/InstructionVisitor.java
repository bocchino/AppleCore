/**
 * A class for visiting instructions.
 */

package AppleCoreCompiler.AVM;

import AppleCoreCompiler.AVM.Instruction.*;

public class InstructionVisitor implements Visitor {

    public void visitBRKInstruction(BRKInstruction inst) { visitInstruction(inst); }
    public void visitBRFInstruction(BRFInstruction inst) { visitInstruction(inst); }
    public void visitBRUInstruction(BRUInstruction inst) { visitInstruction(inst); }
    public void visitCFDInstruction(CFDInstruction inst) { visitInstruction(inst); }
    public void visitCFIInstruction(CFIInstruction inst) { visitInstruction(inst); }
    public void visitADDInstruction(ADDInstruction inst) { visitSizedInstruction(inst); }
    public void visitANLInstruction(ANLInstruction inst) { visitSizedInstruction(inst); }
    public void visitDCRInstruction(DCRInstruction inst) { visitSizedInstruction(inst); }
    public void visitDSPInstruction(DSPInstruction inst) { visitSizedInstruction(inst); }
    public void visitICRInstruction(ICRInstruction inst) { visitSizedInstruction(inst); }
    public void visitISPInstruction(ISPInstruction inst) { visitSizedInstruction(inst); }
    public void visitMTSInstruction(MTSInstruction inst) { visitSizedInstruction(inst); }
    public void visitMTVInstruction(MTVInstruction inst) { visitSizedInstruction(inst); }
    public void visitNEGInstruction(NEGInstruction inst) { visitSizedInstruction(inst); }
    public void visitNOTInstruction(NOTInstruction inst) { visitSizedInstruction(inst); }
    public void visitORLInstruction(ORLInstruction inst) { visitSizedInstruction(inst); }
    public void visitORXInstruction(ORXInstruction inst) { visitSizedInstruction(inst); }
    public void visitPHCInstruction(PHCInstruction inst) { visitSizedInstruction(inst); }
    public void visitPVAInstruction(PVAInstruction inst) { visitSizedInstruction(inst); }
    public void visitRAFInstruction(RAFInstruction inst) { visitSizedInstruction(inst); }
    public void visitSHLInstruction(SHLInstruction inst) { visitSizedInstruction(inst); }
    public void visitSTMInstruction(STMInstruction inst) { visitSizedInstruction(inst); }
    public void visitSUBInstruction(SUBInstruction inst) { visitSizedInstruction(inst); }
    public void visitTEQInstruction(TEQInstruction inst) { visitSizedInstruction(inst); }
    public void visitVTMInstruction(VTMInstruction inst) { visitSizedInstruction(inst); }
    public void visitDIVInstruction(DIVInstruction inst) { visitSignedInstruction(inst); }
    public void visitEXTInstruction(EXTInstruction inst) { visitSignedInstruction(inst); }
    public void visitMULInstruction(MULInstruction inst) { visitSignedInstruction(inst); }
    public void visitSHRInstruction(SHRInstruction inst) { visitSignedInstruction(inst); }
    public void visitTGEInstruction(TGEInstruction inst) { visitSignedInstruction(inst); }
    public void visitTGTInstruction(TGTInstruction inst) { visitSignedInstruction(inst); }
    public void visitTLEInstruction(TLEInstruction inst) { visitSignedInstruction(inst); }
    public void visitTLTInstruction(TLTInstruction inst) { visitSignedInstruction(inst); }
    public void visitLabelInstruction(LabelInstruction inst) { visitInstruction(inst); }
    public void visitCommentInstruction(CommentInstruction inst) { visitInstruction(inst); }
    public void visitNativeInstruction(NativeInstruction inst) { visitInstruction(inst); }

    public void visitSizedInstruction(SizedInstruction inst) { visitInstruction(inst); }
    public void visitSignedInstruction(SignedInstruction inst) { visitInstruction(inst); }

    public void visitInstruction(Instruction inst) {}

}