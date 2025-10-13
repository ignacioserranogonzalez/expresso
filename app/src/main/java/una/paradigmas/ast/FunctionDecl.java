package una.paradigmas.ast;

import java.util.List;

public record FunctionDecl(String name, List<Id> params, Node body) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitFunctionDecl(this);
    }
}
