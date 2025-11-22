package una.paradigmas.node;

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

import una.paradigmas.ast.Visitor;
import java.util.List;

public record ConstructorInvocation(String id, List<Node> args) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitConstructorInvocation(this);
    }
}
