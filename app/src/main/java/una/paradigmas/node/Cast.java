package una.paradigmas.node;
/**
 * Proyecto: Expresso - Transpilador de lenguaje Expresso a Java
 * Curso: [EIF400-II-2025] Paradigmas de Programacion
 * Universidad Nacional de Costa Rica
 * 
 * Autores:
 * - Kendall Miso Chinchilla Araya  -   119310542
 * - Ignacio Serrano Gonzalez       -   402600631
 * - Minor Brenes Aguilar           -   116730106
 * - Pablo Chavarria Alvarez        -   117810573
 * 
 * Codigo de grupo: 02-1PM
 */
import una.paradigmas.ast.Visitor;

public record Cast(Node expr, Node type) implements Node {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCast(this);
    }
}