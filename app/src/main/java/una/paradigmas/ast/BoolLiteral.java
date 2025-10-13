package una.paradigmas.ast;

public record BoolLiteral(boolean value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBool(this);
    }
}
