package una.paradigmas.ast;

public record Comment(String text) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitComment(this);
    }
}