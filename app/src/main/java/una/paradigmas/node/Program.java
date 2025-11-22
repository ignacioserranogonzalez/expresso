package una.paradigmas.node;

import java.util.List;
import java.util.Map;

import una.paradigmas.ast.SymbolTable;
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

public record Program(List<Node> statements, 
                    SymbolTable symbolTable, 
                    Map<String, SymbolTable> contextMap) implements Node {

    public Program {
        statements = List.copyOf(statements);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}