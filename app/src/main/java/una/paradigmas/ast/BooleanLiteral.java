package una.paradigmas.ast;

public record BooleanLiteral(boolean value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBoolean(this);
    }
}
