package una.paradigmas.ast;

record MultDiv(Node left, Node right, String op) implements BinaryOp {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitMultDiv(this);
    }
}
