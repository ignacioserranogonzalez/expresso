package una.paradigmas.node;
import una.paradigmas.ast.Visitor;

public record NoneLiteral() implements Node {
    @Override public <T> T accept(Visitor<T> v) { return v.visitNone(this); }
}
