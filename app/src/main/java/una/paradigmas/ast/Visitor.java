package una.paradigmas.ast;

public interface Visitor<T> {
    T visitNum(Num num);
    // T visitId(Id id);
    T visitBinaryOp(BinaryOp binOp);
    // T visitUnaryOp(UnaryOp unOp);
    // T visitLet(Let let);
    // T visitPrint(Print print);
    // T visitLambda(Lambda lambda);
    // T visitApply(Apply apply);
    T visitProgram(Program program);
}
