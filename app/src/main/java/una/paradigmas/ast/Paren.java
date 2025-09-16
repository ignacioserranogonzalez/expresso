package una.paradigmas.ast;

public record Paren(Node expr) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitParen(this);
    }
}