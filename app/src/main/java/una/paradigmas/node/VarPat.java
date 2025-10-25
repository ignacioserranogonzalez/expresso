package una.paradigmas.node;
import una.paradigmas.ast.Visitor;

public record VarPat(String name) implements Pattern {
    @Override public <T> T accept(Visitor<T> v) { return v.visitVarPat(this); }
}