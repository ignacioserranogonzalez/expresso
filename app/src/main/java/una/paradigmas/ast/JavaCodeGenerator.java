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

        CodeBuilderState state = new CodeBuilderState(new StringBuilder(), new StringBuilder());

        statlist.forEach(line -> {
            boolean isComment = line.startsWith("//") || line.startsWith("/*");
            if (isComment && state.mainCode.length() == 0) {
                state.comments.append(line).append("\n");
            } else {
                state.mainCode.append("        ")
                              .append(isComment ? line : line + ";")
                              .append("\n");
            }
        });       

        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(state.comments); // Añade comentarios iniciales

        // Añadir imports necesarios
        if (!imports.isEmpty()) {
            imports.forEach(imp -> codeBuilder.append("import ").append(imp).append(";\n"));
            codeBuilder.append("\n");
        }

        codeBuilder.append("public class ").append(className).append(" {\n");

        // Añadir métodos auxiliares
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
                String varType = lambdaType(value);
                String result = varType + " " + generateExpression(id) + " = " + valueCode;
                yield comment.text().isEmpty() ? result : result + " " + comment.text();
            }

            case Print(var expr, var comment) -> {
                extraMethods.add("print");
                String result = "print(" + generateExpression(expr) + ")";
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

            case Lambda(var args, var body) -> {
                imports.add("java.util.function.*");
                String params = args.stream()
                    .map(Id::value)
                    .reduce((a, b) -> a + ", " + b)
                    .map(s -> args.size() == 1 ? s : "(" + s + ")")
                    .orElse("()");
                
                yield params + " -> " + generateExpression(body);
            }

            case Call(var id, var param) -> generateExpression(id) + ".apply(" + generateExpression(param) + ")";

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
            default -> "int";
        };
    }
}