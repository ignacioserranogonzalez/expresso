package una.paradigmas.ast;

record Pow(Node left, Node right) implements BinaryOp {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPow(this);
    }
}