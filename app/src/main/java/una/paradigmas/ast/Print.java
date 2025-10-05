package una.paradigmas.ast;

public record Print(Node expr, Comment comment) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPrint(this);
    }
}