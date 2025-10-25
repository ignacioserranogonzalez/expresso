package una.paradigmas.node;

import java.util.List;

import una.paradigmas.ast.Visitor;

public record ConstructorPattern(String name, List<String> vars) implements Pattern {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitConstructorPattern(this);
    }
}
