package una.paradigmas.node;
import una.paradigmas.ast.Visitor;
import java.util.List;

public record Match(Node scrutinee, java.util.List<Case> cases) implements Node {
    public record Case(Pattern pattern, Node guard, Node body) {}
    @Override public <T> T accept(Visitor<T> v) { return v.visitMatch(this); }
}
