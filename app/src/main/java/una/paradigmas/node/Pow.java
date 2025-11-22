package una.paradigmas.node;

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

public record Pow(Node left, Node right) implements BinaryOp {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPow(this);
    }
}