package una.paradigmas.ast;

public record UnaryOp(String op, Node num) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitUnaryOp(this);
    }
}
