package una.paradigmas.ast;

public record BinaryOp(String op, Node left, Node right) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBinaryOp(this);
    }
}