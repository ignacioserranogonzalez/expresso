package una.paradigmas.ast;

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

import java.util.List;

// Usar List<Id> para parámetros y Node para el tipo de retorno
public record Fun(Id name, List<Id> params, Node returnType, Node body) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitFun(this);
    }
}
