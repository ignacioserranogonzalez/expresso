package una.paradigmas.ast;

public record Call(Id id, Node expr) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCall(this);
    }
}

