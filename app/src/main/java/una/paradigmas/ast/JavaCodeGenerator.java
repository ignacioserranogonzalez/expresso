package una.paradigmas.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaCodeGenerator {

    private final Set<String> imports = new HashSet<>();
    private final Set<String> extraMethods = new HashSet<>();
    private final String className;

    public JavaCodeGenerator(String className) {
        this.className = className.toUpperCase().charAt(0) + className.substring(1);
    }

    public String generate(Program ast) {
        // Acumulador para construir el código: (comentariosIniciales, codigoMain)
        record CodeBuilderState(StringBuilder comments, StringBuilder mainCode) {}
        
        List<String> statlist = ast.statements().stream()
                .map(this::generateStatement)
                .toList();

        CodeBuilderState initialState = new CodeBuilderState(new StringBuilder(), new StringBuilder());
        CodeBuilderState finalState = statlist.stream()
                .reduce(initialState, (state, line) -> {
                    if (line.startsWith("//") && state.mainCode.length() == 0) {
                        // Añade comentarios iniciales fuera de main
                        state.comments.append(line).append("\n");
                        return state;
                    } else {
                        // Añade el resto dentro de main
                        state.mainCode.append("        ")
                                      .append(line.startsWith("//") ? line : line + ";")
                                      .append("\n");
                        return state;
                    }
                }, (s1, s2) -> s1); // No se usa en secuencia, pero necesario para el reduce

        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(finalState.comments); // Añade comentarios iniciales

        if (imports.contains("java.util.function.UnaryOperator")) {
            codeBuilder.append("import java.util.function.UnaryOperator;\n");
        }
        codeBuilder.append("public class ").append(className).append(" {\n");

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
        codeBuilder.append(finalState.mainCode); // Añade el código de main
        codeBuilder.append("    }\n" + "}\n");

        return codeBuilder.toString();
    }

    private String generateStatement(Node stat) {
        return switch (stat) {
            case Let(var id, var value) -> {
                String valueCode = generateExpression(value);
                String varType = (value instanceof Lambda) ? "UnaryOperator<Integer>" : "int";
                yield varType + " " + generateExpression(id) + " = " + valueCode;
            }
            case Print(var expr) -> {
                extraMethods.add("print");
                yield "print(" + generateExpression(expr) + ")";
            }
            case Comment(var text) -> {
                yield text.startsWith("//") ? text : "// " + text;
            }
            default -> "";
        };
    }

    private String generateExpression(Node expr) {
        return switch (expr) {
            case IntLiteral(var value) -> Integer.toString(value);
            case FloatLiteral(var value) -> String.valueOf(value);
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
            case Paren(var expr1) ->
                generateExpression(expr1);
            // case Lambda(var id, var expr1) -> {
            //     imports.add("java.util.function.UnaryOperator");
            //     yield generateExpression(id) + " -> " + generateExpression(expr1);
            // }
            case Call(var id, var expr1) ->
                generateExpression(id) + ".apply(" + generateExpression(expr1) + ")";
            default -> "";
        };
    }
}