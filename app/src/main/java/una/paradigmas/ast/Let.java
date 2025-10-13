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

/*public record Let(Id id, Node value) implements Node {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLet(this);
    }
}*/
public record Let(Id id, Node value, String type) implements Node {
    
    // Constructor para declaraciones sin tipo (backward compatibility)
    public Let(Id id, Node value) {
        this(id, value, "any");
    }
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLet(this);
    }
}