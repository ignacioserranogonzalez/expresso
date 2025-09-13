package una.paradigmas.ast;

public interface Node { // clase para nodos (statements y expressions)
    public <T> T accept(Visitor<T> visitor);
}