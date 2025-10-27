package una.paradigmas.node;

import una.paradigmas.ast.Visitor;

public record PrintExpr(Node expr) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPrintExpr(this);
    }
}