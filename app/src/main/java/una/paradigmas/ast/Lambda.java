package una.paradigmas.ast;

public record Lambda(Id id, Node expr) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLambda(this);
    }
}