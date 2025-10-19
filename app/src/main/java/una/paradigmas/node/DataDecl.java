package una.paradigmas.node;

import java.util.List;

import una.paradigmas.ast.Visitor;

public record DataDecl(String id, List<DataDecl.Constructor> constructors) implements Node {
    
    public record Constructor(String id, List<DataDecl.Argument> arguments) {}
    public record Argument(String name, Node type) {}
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDataDecl(this);
    }
}