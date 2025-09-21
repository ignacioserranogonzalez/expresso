package una.paradigmas.ast;

public interface Visitor<T> {
    T visitInt(IntLiteral num);
    T visitFloat(FloatLiteral num);
    T visitId(Id id);
    T visitPow(Pow pow);
    T visitMultDiv(MultDiv multDiv);
    T visitAddSub(AddSub addSub);
    T visitUnaryOp(UnaryOp unOp);
    T visitPostOp(PostOp postOp);
    T visitParen(Paren paren);
    T visitLet(Let let);
    T visitPrint(Print print);
    T visitLambda(Lambda lambda);
    T visitCall(Call call);
    T visitProgram(Program program);
    T visitComment(Comment comment);
}
