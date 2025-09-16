package una.paradigmas.ast;

record AddSub(Node left, Node right, String op) implements BinaryOp {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitAddSub(this);
    }
}
