package una.paradigmas.ast;

import java.util.List;

public record Call(Id id, List<Node> args) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCall(this);
    }
}

