package una.paradigmas.node;

import java.util.List;

import una.paradigmas.ast.Visitor;

/**
 * Proyecto: Expresso - Transpilador de lenguaje Expresso a Java
 * Curso: [EIF400-II-2025] Paradigmas de Programacion
 * Universidad Nacional de Costa Rica
 * 
 * Autores:
 * - Ignacio Serrano Gonzalez
 * - Kendall Miso Chinchilla Araya
 * - Minor Brenes Aguilar
 * 
 * Codigo de grupo: 02-1PM
 */

public record DataDecl(String id, List<DataDecl.Constructor> constructors) implements Node {
    
    public record Constructor(String id, List<DataDecl.Argument> arguments) {}
    public record Argument(String name, Node type) {}
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDataDecl(this);
    }
}