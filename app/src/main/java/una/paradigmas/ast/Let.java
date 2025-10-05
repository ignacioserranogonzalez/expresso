package una.paradigmas.ast;

public record Let(Id id, Node value, Comment comment) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLet(this);
    }
}