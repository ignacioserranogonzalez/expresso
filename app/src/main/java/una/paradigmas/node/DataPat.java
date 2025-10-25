package una.paradigmas.node;
import una.paradigmas.ast.Visitor;
import java.util.List;

public record DataPat(String id, java.util.List<Pattern> args) implements Pattern {
    @Override public <T> T accept(Visitor<T> v) { return v.visitDataPat(this); }
}
