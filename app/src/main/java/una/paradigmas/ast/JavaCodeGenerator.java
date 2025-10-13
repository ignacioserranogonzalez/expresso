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
        if (extraMethods.contains("powDouble")) {
            codeBuilder.append("    public static double powDouble(double x, double e) {\n");
            codeBuilder.append("        return Math.pow(x, e);\n");
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
            case Let let -> {
                String valueCode = generateExpression(let.value());
                String varName = generateExpression(let.id());
                
                // Determinar el tipo basado en el valor
                String varType = inferType(let.value());
                
                yield varType + " " + varName + " = " + valueCode + ";";
            }

            case Print(var expr) -> {
                extraMethods.add("print");
                yield "print(" + generateExpression(expr) + ");";
            }

            case FunctionDecl function -> {
                // Generar código para declaración de función
                yield generateFunction(function);
            }

            default -> "";
        };
    }

    private String generateExpression(Node expr) {
        return switch (expr) {
            case IntLiteral(var value) -> Integer.toString(value);
            
            case FloatLiteral(var value) -> Double.toString(value);
            
            case BooleanLiteral(var value) -> Boolean.toString(value);
            
            case StringLiteral(var value) -> {
                // Escapar el string para Java
                String escapedValue = value
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\t", "\\t")
                    .replace("\r", "\\r");
                yield "\"" + escapedValue + "\"";
            }

            case Id(var value) -> value;

            case Pow(var left, var right) -> {
                // Determinar si necesitamos pow para enteros o doubles
                String leftType = inferType(left);
                String rightType = inferType(right);
                
                if (leftType.equals("Double") || rightType.equals("Double")) {
                    extraMethods.add("powDouble");
                    yield "powDouble(" + generateExpression(left) + ", " + generateExpression(right) + ")";
                } else {
                    extraMethods.add("pow");
                    yield "pow(" + generateExpression(left) + ", " + generateExpression(right) + ")";
                }
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

            case TernaryCondition(var condition, var value1, var value2) -> {
                String condExpr = generateExpression(condition);
                String trueExpr = generateExpression(value1);
                String falseExpr = generateExpression(value2);
                
                // Para booleanos, usar condición directa
                if (condition instanceof BooleanLiteral) {
                    yield "(" + condExpr + " ? " + trueExpr + " : " + falseExpr + ")";
                } else {
                    // Para números, usar != 0 como antes
                    yield "(" + condExpr + " != 0 ? " + trueExpr + " : " + falseExpr + ")";
                }
            }

            case Lambda(var args, var body) -> {
                imports.add("java.util.function.*");
                String params = args.stream()
                    .map(Id::value)
                    .reduce((a, b) -> a + ", " + b)
                    .map(s -> args.size() == 1 ? s : "(" + s + ")")
                    .orElse("()");
                
                // Determinar el tipo de retorno de la lambda
                String returnType = inferType(body);
                String functionType = getFunctionType(args.size(), returnType);
                
                yield functionType + " " + params + " -> " + generateExpression(body);
            }

            case Call(var id, var paramList) -> {
                String params = paramList.stream()
                    .map(this::generateExpression)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                yield generateExpression(id) + ".apply(" + params + ")";
            }

            default -> throw new IllegalArgumentException("Expresión no soportada: " + expr.getClass().getSimpleName());
        };
    }

    // Inferir el tipo Java basado en el nodo
    private String inferType(Node expr) {
        return switch (expr) {
            case IntLiteral i -> "Integer";
            case FloatLiteral f -> "Double";
            case BooleanLiteral b -> "Boolean";
            case StringLiteral s -> "String";
            case Lambda l -> {
                String returnType = inferType(l.expr());
                yield getFunctionType(l.args().size(), returnType);
            }
            default -> "Object";
        };
    }

    // Obtener el tipo de función para lambdas
    private String getFunctionType(int argCount, String returnType) {
        return switch (argCount) {
            case 0 -> "Supplier<" + returnType + ">";
            case 1 -> "Function<Object, " + returnType + ">";
            case 2 -> "BiFunction<Object, Object, " + returnType + ">";
            default -> "Function<Object[], " + returnType + ">"; // Para más argumentos
        };
    }

    // Generar declaraciones de función
    private String generateFunction(FunctionDecl function) {
        imports.add("java.util.function.*");
        
        String returnType = inferType(function.body());
        String params = function.params().stream()
            .map(param -> "Object " + generateExpression(param))
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        
        String functionType = getFunctionType(function.params().size(), returnType);
        
        return functionType + " " + function.name() + " = (" + params + ") -> " + 
               generateExpression(function.body()) + ";";
    }
}