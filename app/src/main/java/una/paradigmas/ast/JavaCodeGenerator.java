package una.paradigmas.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
 * 
 * Nota: Este codigo fue generado parcialmente con asistencia de IA
 * y posteriormente modificado, adaptado y validado por el equipo
 * de desarrollo para cumplir con los requerimientos especificos
 * del proyecto.
 */

public class JavaCodeGenerator {

    private final Set<String> imports = new HashSet<>();
    private final Set<String> extraMethods = new HashSet<>();
    private final String className;

    public JavaCodeGenerator(String className) {
        this.className = className.toUpperCase().charAt(0) + className.substring(1);
    }

    public String generate(Program ast) {
        // Acumulador para construir el código
        record CodeBuilderState(StringBuilder comments, StringBuilder mainCode) {}
        
        List<String> statlist = ast.statements().stream()
                .map(this::generateStatement)
                .toList();

        CodeBuilderState state = new CodeBuilderState(new StringBuilder(), new StringBuilder());

        statlist.forEach(line -> state.mainCode.append("        ").append(line).append("\n"));

        StringBuilder codeBuilder = new StringBuilder();

        // Imports necesarios
        if (!imports.isEmpty()) {
            imports.forEach(imp -> codeBuilder.append("import ").append(imp).append(";\n"));
            codeBuilder.append("\n");
        }

        codeBuilder.append("public class ").append(className).append(" {\n");

        // Métodos auxiliares
        if (extraMethods.contains("pow")) {
            codeBuilder.append("    public static int pow(int x, int e) {\n")
                       .append("        return (int)Math.pow(x, e);\n")
                       .append("    }\n");
        }
        if (extraMethods.contains("print")) {
            codeBuilder.append("    public static void print(Object arg) {\n")
                       .append("        System.out.println(arg);\n")
                       .append("    }\n");
        }

        codeBuilder.append("    public static void main(String... args) {\n")
                   .append(state.mainCode)
                   .append("    }\n}\n");

        return codeBuilder.toString();
    }

    private String generateStatement(Node stat) {
        return switch (stat) {
            case Let(var id, var type, var value) -> {
                String valueCode = generateExpression(value);
                String varType = type != null ? mapType(type) : lambdaType(value);
                yield varType + " " + generateExpression(id) + " = " + valueCode + ";";
            }
            case Print(var expr) -> {
                extraMethods.add("print");
                yield "print(" + generateExpression(expr) + ");";
            }
            default -> "";
        };
    }

    private String mapType(String type) {
        return switch (type) {
            case "int" -> "int";
            case "float" -> "double";
            case "boolean", "bool" -> "boolean";
            case "string" -> "String";
            default -> "var";
        };
    }

    private String generateExpression(Node expr) {
        return switch (expr) {
            case IntLiteral(var value) -> Integer.toString(value);
            case FloatLiteral(var value) -> Double.toString(value);
            case BoolLiteral(var value) -> Boolean.toString(value);
            case StringLiteral(var value) -> "\"" + value.replace("\"", "\\\"") + "\"";
            case Id(var name) -> name;
            case AddSub(var left, var op, var right) -> "(" + generateExpression(left) + " " + op + " " + generateExpression(right) + ")";
            case MultDiv(var left, var op, var right) -> "(" + generateExpression(left) + " " + op + " " + generateExpression(right) + ")";
            case Pow(var left, var right) -> {
                extraMethods.add("pow");
                yield "pow(" + generateExpression(left) + ", " + generateExpression(right) + ")";
            }
            case UnaryOp(var op, var inner) -> op + (inner instanceof UnaryOp ? "(" + generateExpression(inner) + ")" : generateExpression(inner));
            case PostOp(var inner, var op) -> generateExpression(inner) + op;
            case Paren(var inner) -> "(" + generateExpression(inner) + ")";
            case TernaryCondition(var cond, var v1, var v2) ->
                "(" + generateExpression(cond) + " != 0 ? " + generateExpression(v1) + " : " + generateExpression(v2) + ")";
            case Lambda(var args, var body) -> {
                imports.add("java.util.function.*");
                String params = args.stream()
                        .map(Id::value)
                        .reduce((a, b) -> a + ", " + b)
                        .map(s -> args.size() == 1 ? s : "(" + s + ")")
                        .orElse("()");
                yield params + " -> " + generateExpression(body);
            }
            case Call(var id, var args) -> {
                String params = args.stream()
                        .map(this::generateExpression)
                        .collect(Collectors.joining(", "));
                yield generateExpression(id) + ".apply(" + params + ")";
            }
            default -> throw new IllegalArgumentException("Expresión no soportada: " + expr.getClass().getSimpleName());
        };
    }

    private String lambdaType(Node expr) {
        return switch (expr) {
            case IntLiteral ignored -> "int";
            case FloatLiteral ignored -> "double";
            case BoolLiteral ignored -> "boolean";
            case StringLiteral ignored -> "String";
            case Id ignored -> "var";
            case AddSub a -> lambdaType(a.left());
            case MultDiv m -> lambdaType(m.left());
            case Pow p -> lambdaType(p.left());
            case UnaryOp u -> lambdaType(u.expr());
            case PostOp p -> lambdaType(p.expr());
            case Paren p -> lambdaType(p.expr());
            case TernaryCondition t -> lambdaType(t.value1());
            case Call c -> "var"; // fallback para llamadas
            case Lambda l -> {
                imports.add("java.util.function.*");
                if (l.args().size() == 0) yield "Supplier<Integer>";
                else if (l.args().size() == 1) yield "UnaryOperator<Integer>";
                else yield "BinaryOperator<Integer>";
            }
            default -> throw new IllegalArgumentException("Expresión no soportada: " + expr.getClass().getSimpleName());
        };
    }

}
