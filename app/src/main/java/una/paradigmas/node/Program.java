package una.paradigmas.node;

import java.util.List;
import java.util.Set;

import una.paradigmas.ast.SymbolTable;
import una.paradigmas.ast.Visitor;

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

public record Program(List<Node> statements, SymbolTable symbolTable) implements Node {

    public Program {
        statements = List.copyOf(statements);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}