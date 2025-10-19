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
    private final StringBuilder sealedTypes = new StringBuilder();

    public JavaCodeGenerator(String className) {
        this.className = capitalizeFirst(className);
    }

    public String generate(Program ast) {
        record CodeBuilderState(StringBuilder mainCode) {}
        
        sealedTypes.setLength(0);
        
        List<DataDecl> dataDecls = ast.statements().stream()
            .filter(statement -> statement instanceof DataDecl)
            .map(statement -> (DataDecl) statement)
            .toList();

        List<String> statlist = ast.statements().stream()
                .map(this::generateStatement)
                .toList();

        CodeBuilderState state = new CodeBuilderState(new StringBuilder());

        dataDecls.forEach(dataDecl -> generateDataDecl(dataDecl.id(), dataDecl.constructors()));

        statlist.forEach(line -> {
            if (!line.isEmpty()) {
                state.mainCode.append("        ")
                .append(line)
                .append("\n");
            }
        });       

        StringBuilder codeBuilder = new StringBuilder();

        // imports necesarios
        if (!imports.isEmpty()) {
            imports.forEach(imp -> codeBuilder.append("import ").append(imp).append(";\n"));
            codeBuilder.append("\n");
        }

        codeBuilder.append("public class ").append(className).append(" {\n");

        // Tipos algebraicos (sealed interfaces y records)
        if (sealedTypes.length() > 0) {
            codeBuilder.append(sealedTypes);
        }

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
        codeBuilder.append(state.mainCode);
        codeBuilder.append("    }\n}\n");

        return codeBuilder.toString();
    }

    private void generateDataDecl(String dataId, List<DataDecl.Constructor> constructors) {
        String typeName = capitalizeFirst(dataId);
        
        String permits = constructors.stream()
            .map(DataDecl.Constructor::id)
            .map(this::capitalizeFirst)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        
        sealedTypes.append("    sealed interface ").append(typeName)
            .append(" permits ").append(permits).append(" {}\n");
        
        constructors.forEach(constructor -> {
            String constructorName = capitalizeFirst(constructor.id());
            
            if (constructor.arguments().isEmpty()) {
                sealedTypes.append("    record ").append(constructorName)
                    .append("() implements ").append(typeName).append(" {}\n");
            } else {
                String argParams = constructor.arguments().stream()
                    .map(arg -> {
                        String argType = getJavaTypeFromNode(arg.type());
                        String argName = arg.name().isEmpty() 
                            ? "arg" + arg.hashCode() % 1000 
                            : arg.name();
                        return argType + " " + argName;
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                
                sealedTypes.append("    record ").append(constructorName)
                    .append("(").append(argParams).append(") implements ")
                    .append(typeName).append(" {}\n");
            }
        });
        
        sealedTypes.append("\n");
    }

    private String getJavaTypeFromNode(Node typeNode) {
        return switch (typeNode) {
            case TypeNode(String typeName) -> switch (typeName) {
                case "int" -> "int";
                case "float" -> "float";
                case "boolean" -> "boolean";
                case "string" -> "String";
                case "any" -> "Object";
                default -> capitalizeFirst(typeName);
            };
            default -> "Object";
        };
    }
   
    private String generateStatement(Node stat) {
        return switch (stat) {
            case Let(Id id, Node value, Node typeNode) -> 
                getJavaType(typeNode, value) + " " + generateExpression(id) + " = " + generateExpression(value) + ";";

            case Print(Node expr) -> {
                extraMethods.add("print");
                yield "print(" + generateExpression(expr) + ");";
            }
            
            case DataDecl(String id, List<DataDecl.Constructor> constructors) -> "";

            default -> "";
        };
    }

    private String generateExpression(Node expr) {
        return switch (expr) {
            case IntLiteral(int value) -> Integer.toString(value);
            case FloatLiteral(float value) -> value + "f";
            case BooleanLiteral(boolean value) -> Boolean.toString(value);
            case StringLiteral(String value) -> "\"" + escapeString(value) + "\"";

            case Id(String value) -> value;

            case Pow(Node left, Node right) -> {
                extraMethods.add("pow");
                yield "pow(" + generateExpression(left) + ", " + generateExpression(right) + ")";
            }

            case MultDiv(Node left, String op, Node right) ->
                generateExpression(left) + " " + op + " " + generateExpression(right);

            case AddSub(Node left, String op, Node right) ->
                generateExpression(left) + " " + op + " " + generateExpression(right);

            case UnaryOp(String op, Node expr2) ->
                op + generateExpression(expr2);

            case PostOp(Node expr1, String op) ->
                generateExpression(expr1) + op;

            case Paren(Node value) ->
                "(" + generateExpression(value) + ")";

            case TernaryCondition(Node condition, Node value1, Node value2) -> 
                "(" + generateExpression(condition) + " != 0 ? " 
                    + generateExpression(value1) + " : " 
                    + generateExpression(value2) + ")";

            case Lambda(List<Id> args, Node body) -> {
                imports.add("java.util.function.*");
                String params = args.stream()
                    .map(Id::value)
                    .reduce((a, b) -> a + ", " + b)
                    .map(s -> args.size() == 1 ? s : "(" + s + ")")
                    .orElse("()");
                
                yield params + " -> " + generateExpression(body);
            }

            case Call(Id id, List<Node> paramList) -> {
                String params = paramList.stream()
                    .map(this::generateExpression)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                yield generateExpression(id) + ".apply(" + params + ")";
            }

            default -> throw new IllegalArgumentException("Expresión no soportada: " + expr.getClass().getSimpleName());
        };
    }
    
    private String getJavaType(Node typeNode, Node value) {
        return switch (typeNode) {
            case TypeNode(String typeName) -> switch (typeName) {
                case "int" -> "int";
                case "float" -> "float";
                case "boolean" -> "boolean";
                case "string" -> "String";
                case "any" -> inferTypeFromValue(value);
                default -> "Object";
            };
            default -> inferTypeFromValue(value);
        };
    }
    
    private String inferTypeFromValue(Node value) {
        return switch (value) {
            case IntLiteral _ -> "int";
            case FloatLiteral _ -> "float";
            case BooleanLiteral _ -> "boolean";
            case StringLiteral _ -> "String";
            case Lambda _ -> lambdaType(value);
            default -> "Object";
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
    
    private String escapeString(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\t", "\\t")
                    .replace("\r", "\\r")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f");
    }

    private String capitalizeFirst(String str) {
        return str == null || str.isEmpty() ? str 
            : str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}