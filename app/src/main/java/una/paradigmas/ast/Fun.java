package una.paradigmas.ast;
import java.util.List;

public record Fun(String name, List<TypedId> params, String returnType, Node body) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitFun(this);
    }
}
