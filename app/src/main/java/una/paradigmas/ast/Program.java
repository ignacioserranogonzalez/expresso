package una.paradigmas.ast;

import java.util.List;

public record Program(List<Node> statements) implements Node {
    public Program {
        statements = List.copyOf(statements); // Inmutabilidad
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}