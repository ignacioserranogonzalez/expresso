package una.paradigmas.node;
import una.paradigmas.ast.Visitor;

public record NativePat(Node value) implements Pattern { 
    @Override public <T> T accept(Visitor<T> v) { return v.visitNativePat(this); }
}
