package una.paradigmas.ast;

public record Num(int value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitNum(this);
    }
}