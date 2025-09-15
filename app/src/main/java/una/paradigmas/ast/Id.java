package una.paradigmas.ast;

public record Id(String value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitId(this);
    }
}