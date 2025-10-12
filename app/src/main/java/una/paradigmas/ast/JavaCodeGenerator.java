package una.paradigmas.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final Set<String> lambdaVars = new HashSet<>(); // Nuevo conjunto para rastrear variables lambda
    private final String className;

    public JavaCodeGenerator(String className) {
        this.className = className.toUpperCase().charAt(0) + className.substring(1);
    }

    public String generate(Program ast) {
        // Acumulador para construir el codigo (comentariosIniciales, codigoMain)
        record CodeBuilderState(StringBuilder comments, StringBuilder mainCode) {}
        
        // Limpiar lambdaVars antes de procesar un nuevo programa
        lambdaVars.clear();

        List<String> statlist = ast.statements().stream()
                .map(this::generateStatement)
                .toList();

        CodeBuilderState state = new CodeBuilderState(new StringBuilder(), new StringBuilder());

        statlist.forEach(line -> {
            state.mainCode.append("        ")
            .append(line)
            .append("\n");
        });       

        StringBuilder codeBuilder = new StringBuilder();

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

        // Añadir métodos generados por Fun
        ast.statements().stream()
            .filter(stat -> stat instanceof Fun)
            .map(Fun.class::cast) //Esto es una forma segura de casteo
            .forEach(fun -> {
                String paramDecls = fun.params().stream()
                    .map(p -> p.type() + " " + p.name())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                codeBuilder.append("    private static ").append(fun.returnType()).append(" ")
                    .append(fun.name()).append("(").append(paramDecls).append(") {\n");
                String bodyCode = generateExpression(fun.body());
                codeBuilder.append("        ").append(fun.returnType().equals("void") ? "" : "return ")
                    .append(bodyCode).append(";\n");
                codeBuilder.append("    }\n");
            });

        codeBuilder.append("    public static void main(String... args) {\n");
        codeBuilder.append(state.mainCode); // Añade el código de main
        codeBuilder.append("    }\n}\n");

        return codeBuilder.toString();
    }

    private String generateStatement(Node stat) {
        return switch (stat) {
            case Let(var id, var value) -> {
                String valueCode = generateExpression(value);
                String varType = lambdaType(value);
                if (value instanceof Lambda) {
                    addLambdaVar(generateExpression(id));
                }
                yield varType + " " + generateExpression(id) + " = " + valueCode + ";";
            }

            case Print(var expr) -> {
                extraMethods.add("print");
                yield "print(" + generateExpression(expr) + ");";
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

            case UnaryOp(var op, var expr2) -> {
                String inner = generateExpression(expr2);
                // Si el expr2 es otro UnaryOp, agregar paréntesis
                if (inner.startsWith("+") || inner.startsWith("-")) {
                    yield op + "(" + inner + ")";
                }
                yield op + inner;
            }

            case PostOp(var expr1, var op) ->
                generateExpression(expr1) + op;

            case Paren(var value) ->
                "(" + generateExpression(value) + ")";

            case TernaryCondition(var condition, var value1, var value2) -> 
                "(" + generateExpression(condition) + " != 0 ? " 
                    + generateExpression(value1) + " : " 
                    + generateExpression(value2) + ")";

            case Lambda(var args, var body) -> {
                imports.add("java.util.function.*");
                String params = args.stream()
                    .map(arg -> arg.name() + (arg.type() != null && !arg.type().equals("any") ? ":" + arg.type() : ""))
                    .reduce((a, b) -> a + ", " + b)
                    .map(s -> args.size() == 1 ? s : "(" + s + ")")
                    .orElse("()"); 
                yield params + " -> " + generateExpression(body);
            }

            case Call(var id, var paramList) -> {
                String params = paramList.stream()
                    .map(this::generateExpression)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                String call = lambdaVars.contains(id.value()) 
                    ? generateExpression(id) + ".apply(" + params + ")" 
                    : id.value() + "(" + params + ")";
                yield call;
            }

            default -> throw new IllegalArgumentException("Expresión no soportada: " + expr.getClass().getSimpleName());
        };
    }

    private String lambdaType(Node expr) {
        return switch (expr) {
            case Lambda l -> {
                imports.add("java.util.function.*");
                if (l.args().size() == 0) yield "Supplier<Integer>";
                else if (l.args().size() == 1) yield "UnaryOperator<Integer>";
                else yield "BinaryOperator<Integer>";
            }
            case IntLiteral _ -> "int";
            case FloatLiteral _ -> "float";
            default -> "void";
        };
    }

    // Método auxiliar para registrar variables lambda
    private void addLambdaVar(String varName) {
        lambdaVars.add(varName);
    }
}