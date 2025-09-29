package una.paradigmas.ast;

import java.util.List;

public record Lambda(List<Id> args, Node expr) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLambda(this);
    }
}