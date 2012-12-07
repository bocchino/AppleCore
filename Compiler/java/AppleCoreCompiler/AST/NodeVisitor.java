/**
 * A class for visiting one node at a time, and doing something
 * different for each one.
 */

package AppleCoreCompiler.AST;

import AppleCoreCompiler.AST.Node.*;
import AppleCoreCompiler.Errors.*;

public class NodeVisitor implements Visitor {

    public void visitSourceFile(SourceFile node) throws ACCError { visitNode(node); }
    public void visitIncludeDecl(IncludeDecl node) throws ACCError { visitNode(node); }
    public void visitConstDecl(ConstDecl node) throws ACCError { visitNode(node); }
    public void visitDataDecl(DataDecl node) throws ACCError { visitNode(node); }
    public void visitVarDecl(VarDecl node) throws ACCError { visitNode(node); }
    public void visitFunctionDecl(FunctionDecl node) throws ACCError { visitNode(node); }
    public void visitIfStatement(IfStatement node) throws ACCError { visitNode(node); }
    public void visitWhileStatement(WhileStatement node) throws ACCError { visitNode(node); }
    public void visitSetStatement(SetStatement node) throws ACCError { visitNode(node); }
    public void visitIncrStatement(IncrStatement node) throws ACCError { visitNode(node); }
    public void visitDecrStatement(DecrStatement node) throws ACCError { visitNode(node); }
    public void visitCallStatement(CallStatement node) throws ACCError { visitNode(node); }
    public void visitReturnStatement(ReturnStatement node) throws ACCError { visitNode(node); }
    public void visitBlockStatement(BlockStatement node) throws ACCError { visitNode(node); }
    public void visitIndexedExpression(IndexedExpression node) throws ACCError { visitNode(node); }
    public void visitCallExpression(CallExpression node) throws ACCError { visitNode(node); }
    public void visitRegisterExpression(RegisterExpression node) throws ACCError { visitNode(node); }
    public void visitBinopExpression(BinopExpression node) throws ACCError { visitNode(node); }
    public void visitUnopExpression(UnopExpression node) throws ACCError { visitNode(node); }
    public void visitParensExpression(ParensExpression node) throws ACCError { visitNode(node); }
    public void visitSizedExpression(SizedExpression node) throws ACCError { visitNode(node); }
    public void visitIdentifier(Identifier node) throws ACCError { visitNode(node); }
    public void visitCharConstant(CharConstant node) throws ACCError { visitNode(node); }
    public void visitStringConstant(StringConstant node) throws ACCError { visitNode(node); }
    public void visitIntegerConstant(IntegerConstant node) throws ACCError { visitNode(node); }

    public void visitNode(Node node) throws ACCError {}
}