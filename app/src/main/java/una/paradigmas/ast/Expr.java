package una.paradigmas.ast;

public interface Expr { // clase para nodos (statements y expressions)
    public <T> T accept(Visitor<T> visitor);
}