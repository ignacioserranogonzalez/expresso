package una.paradigmas.ast;

public record Num(int value) implements Expr {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitNum(this);
    }
}