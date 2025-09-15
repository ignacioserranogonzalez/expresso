package una.paradigmas.ast;

public record IntLiteral(int value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitInt(this);
    }
}