package una.paradigmas.ast;

record MultDiv(Node left, String op, Node right) implements BinaryOp {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitMultDiv(this);
    }
}
