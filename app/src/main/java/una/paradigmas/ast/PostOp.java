package una.paradigmas.ast;

public record PostOp(Node expr, String op) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPostOp(this);
    }
}

