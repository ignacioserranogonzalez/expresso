package una.paradigmas.ast;

public record BinaryOp(String op, Expr left, Expr right) implements Expr {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBinaryOp(this);
    }
}