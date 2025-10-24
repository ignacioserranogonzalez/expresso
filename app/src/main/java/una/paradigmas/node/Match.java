package una.paradigmas.node;
import java.util.List;
import una.paradigmas.ast.Visitor;
import una.paradigmas.pattern.MatchCase;

public record Match(Node expr, List<MatchCase> cases) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitMatch(this);
    }
}
