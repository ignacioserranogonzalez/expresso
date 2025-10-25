package una.paradigmas.node;
import una.paradigmas.ast.Visitor;

public record WildcardPat() implements Pattern {
    @Override public <T> T accept(Visitor<T> v) { return v.visitWildcardPat(this); }
}
