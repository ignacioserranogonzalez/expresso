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
        // Acumulador para construir el codigo (comentariosIniciales, codigoMain)
        record CodeBuilderState(StringBuilder comments, StringBuilder mainCode) {}
        
        List<String> statlist = ast.statements().stream()
                .map(this::generateStatement)
                .toList();

        CodeBuilderState state = new CodeBuilderState(new StringBuilder(), new StringBuilder());

        statlist.forEach(line -> {
            boolean isComment = line.startsWith("//") || line.startsWith("/*");
            if (isComment && state.mainCode.length() == 0) {
                state.comments.append(line).append("\n");
            } else {
                state.mainCode.append("        ")
                              .append(line)
                              .append("\n");
            }
        });       

        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(state.comments); // Añade comentarios iniciales

        // imports necesarios
        if (!imports.isEmpty()) {
            imports.forEach(imp -> codeBuilder.append("import ").append(imp).append(";\n"));
            codeBuilder.append("\n");
        }

        codeBuilder.append("public class ").append(className).append(" {\n");

        // Metodos auxiliares
        if (extraMethods.contains("pow")) {
            codeBuilder.append("    public static int pow(int x, int e) {\n");
            codeBuilder.append("        return (int)Math.pow(x, e);\n");
            codeBuilder.append("    }\n");
        }
        if (extraMethods.contains("print")) {
            codeBuilder.append("    public static void print(Object arg) {\n");
            codeBuilder.append("        System.out.println(arg);\n");
            codeBuilder.append("    }\n");
        }

        codeBuilder.append("    public static void main(String... args) {\n");
        codeBuilder.append(state.mainCode); // Añade el código de main
        codeBuilder.append("    }\n}\n");

        return codeBuilder.toString();
    }

    private String generateStatement(Node stat) {
        return switch (stat) {
            case Let(var id, var value, var comment) -> {
                String valueCode = generateExpression(value);
                String varType = switch (value) {
                    case Lambda l -> lambdaType(l);
                    default -> "int";
                };
                
                String result = varType + " " + generateExpression(id) + " = " + valueCode + ";";
                yield comment.text().isEmpty() ? result : result + " " + comment.text();
            }

            case Print(var expr, var comment) -> {
                extraMethods.add("print");
                String result = "print(" + generateExpression(expr) + ");";
                yield comment.text().isEmpty() ? result : result + " " + comment.text();
            }

            case Comment(var text) -> {
                if (text.startsWith("/*")) {
                    yield text;
                } else {
                    yield text.startsWith("//") ? text : "// " + text;
                }
            }

            default -> "";
        };
    }

    private String generateExpression(Node expr) {
        return switch (expr) {
            case IntLiteral(var value) -> Integer.toString(value);

            case Id(var value) -> value;

            case Pow(var left, var right) -> {
                extraMethods.add("pow");
                yield "pow(" + generateExpression(left) + ", " + generateExpression(right) + ")";
            }

            case MultDiv(var left, var op, var right) ->
                generateExpression(left) + " " + op + " " + generateExpression(right);

            case AddSub(var left, var op, var right) ->
                generateExpression(left) + " " + op + " " + generateExpression(right);

            case UnaryOp(var op, var num) ->
                op + generateExpression(num);

            case PostOp(var expr1, var op) ->
                generateExpression(expr1) + op;

            case Paren(var value) ->
                "(" + generateExpression(value) + ")";

            case TernaryCondition(var condition, var value1, var value2) -> 
                "(" + generateExpression(condition) + " != 0 ? " 
                    + generateExpression(value1) + " : " 
                    + generateExpression(value2) + ")";

            case Lambda(var args, var expr2) -> {
                imports.add("java.util.function.*");
                String params = args.stream()
                    .map(Id::value)
                    .reduce((a, b) -> a + ", " + b)
                    .map(s -> args.size() == 1 ? s : "(" + s + ")")
                    .orElse("()");
                
                yield params + " -> " + generateExpression(expr2);
            }

            case Call(var function, var args) -> {
                String funcCode = generateExpression(function);
                String argsCode = args.stream()
                    .map(this::generateExpression)
                    .collect(Collectors.joining(", "));
                yield funcCode + ".apply(" + argsCode + ")";
            }

            default -> throw new IllegalArgumentException("Expresión no soportada: " + expr.getClass().getSimpleName());
        };
    }

    private String lambdaType(Node expr) {
        return switch (expr) {
            case Lambda l -> {
                imports.add("java.util.function.*");
                
                // Determinar el tipo del cuerpo (puede ser otra lambda)
                String exprType = lambdaType(l.expr()); // recursion
                
                // Si el cuerpo es "int", usar operadores simples
                if (exprType.equals("int")) {
                    yield switch (l.args().size()) {
                        case 0 -> "Supplier<Integer>";
                        case 1 -> "UnaryOperator<Integer>";
                        case 2 -> "BinaryOperator<Integer>";
                        default -> "Function<Integer, " + exprType + ">";
                    };
                } 
                // Si el cuerpo es otra función/lambda, usar Function/BiFunction
                else {
                    yield switch (l.args().size()) {
                        case 0 -> "Supplier<" + exprType + ">";
                        case 1 -> "Function<Integer, " + exprType + ">";
                        case 2 -> "BiFunction<Integer, Integer, " + exprType + ">";
                        default -> "Function<Integer, " + exprType + ">";
                    };
                }
            }
            default -> "int";
        };
    }
}