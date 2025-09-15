package una.paradigmas.ast;

public record Let(String id, Node value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLet(this);
    }
}