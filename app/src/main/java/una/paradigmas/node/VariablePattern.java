package una.paradigmas.node;
import java.util.List;
import una.paradigmas.ast.Visitor;

public record VariablePattern(String name) implements Pattern {
    @Override 
    public <T> T accept(Visitor<T> visitor) { 
        return visitor.visitVariablePattern(this); 
    }
}