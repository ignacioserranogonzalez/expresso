package una.paradigmas.ast;

public record TernaryCondition(Node condition, Node value1, Node value2) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitTernaryCondition(this);
    }
}