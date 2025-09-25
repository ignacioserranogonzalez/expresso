package una.paradigmas.ast;

record AddSub(Node left, String op, Node right) implements BinaryOp {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitAddSub(this);
    }
}
