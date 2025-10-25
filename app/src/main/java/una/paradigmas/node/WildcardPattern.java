package una.paradigmas.node;

import una.paradigmas.ast.Visitor;

public record WildcardPattern() implements Pattern {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitWildcardPattern(this); 
    }
}
