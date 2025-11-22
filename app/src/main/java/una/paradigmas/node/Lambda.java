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

public record Lambda(String name, List<Param> params, Node returnType, Node body) implements Node {
    public record Param(Id id, Node type) {}

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLambda(this);
    }
}