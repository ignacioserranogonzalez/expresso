package una.paradigmas.ast;

public record FloatLiteral(float value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitFloat(this);
    }
}