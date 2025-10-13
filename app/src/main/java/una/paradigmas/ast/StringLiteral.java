package una.paradigmas.ast;

public record StringLiteral(String value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitString(this);
    }
}
