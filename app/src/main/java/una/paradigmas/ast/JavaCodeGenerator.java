package una.paradigmas.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.*;
import java.util.function.*;

import una.paradigmas.node.*;
import una.paradigmas.node.Node;

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
    private final String className;
    private final StringBuilder methodDefinitions = new StringBuilder();
    private final Set<String> extraMethods = new HashSet<>();
    private final StringBuilder constructorTypes = new StringBuilder();
    private final StringBuilder mainCodeBuilder = new StringBuilder();
    private final List<DataDecl> dataDeclarations = new ArrayList<>();
    private final Set<String> functionNames = new HashSet<>(); // tabla de simbolos simple ??

     public JavaCodeGenerator(String className) {
        this.className = capitalizeFirst(className);
    }

    public String generate(Program ast) {
        resetBuilders();
        extractDataDeclarations(ast);

        collectFunctionNames(ast);
        
        dataDeclarations.forEach(dataDecl -> 
            generateDataDecl(dataDecl.id(), dataDecl.constructors()));

        generateMethodDefinitions(ast);
        generateMainMethod(ast);
        
        return buildFinalCode();
    }

    private void resetBuilders() {
        imports.clear();
        methodDefinitions.setLength(0);
        constructorTypes.setLength(0);
        mainCodeBuilder.setLength(0);
        extraMethods.clear();
        dataDeclarations.clear();
    }

    private void collectFunctionNames(Program ast) {
        functionNames.clear();
        ast.statements().stream()
            .filter(statement -> statement instanceof Fun)
            .map(statement -> (Fun) statement)
            .forEach(fun -> functionNames.add(fun.name().value()));
    }

    private void extractDataDeclarations(Program ast) {
        dataDeclarations.addAll(ast.statements().stream()
            .filter(statement -> statement instanceof DataDecl)
            .map(statement -> (DataDecl) statement)
            .toList());
    }

    private void generateDataDecl(String dataId, List<DataDecl.Constructor> constructors) {
    String typeName = capitalizeFirst(dataId);
    String permits = constructors.stream()
        .map(DataDecl.Constructor::id)
        .map(this::capitalizeFirst)
        .collect(Collectors.joining(", "));
    
    constructorTypes.append("    sealed interface ")
        .append(typeName)
        .append(" permits ")
        .append(permits)
        .append(" {}\n");

    constructors.forEach(constructor -> {
        String constructorName = capitalizeFirst(constructor.id());
        
        if (constructor.arguments().isEmpty()) {
            constructorTypes.append("    record ")
                .append(constructorName)
                .append("() implements ")
                .append(typeName)
                .append(" {}\n");
        } else {
            var argsList = constructor.arguments();
            String argParams = IntStream.range(0, argsList.size())
                .mapToObj(i -> {
                    var arg = argsList.get(i);
                    String argType = generateType(arg.type());
                    String argName = arg.name().isEmpty() ? "arg" + i : arg.name();
                    return argType + " " + argName;
                })
                .collect(Collectors.joining(", "));
            
            constructorTypes.append("    record ")
                .append(constructorName)
                .append("(")
                .append(argParams)
                .append(") implements ")
                .append(typeName)
                .append(" {}\n");
        }
    });
    
    constructorTypes.append("\n");
    }

    private void generateMethodDefinitions(Program ast) {
        List<String> functionStatements = ast.statements().stream()
            .filter(statement -> statement instanceof Fun)
            .map(statement -> generateStatement(statement))
            .filter(line -> !line.isBlank())
            .toList();
            
        functionStatements.forEach(line -> 
            methodDefinitions.append("    ").append(line).append("\n"));
    }

    private void generateMainMethod(Program ast) {
        List<String> mainStatements = ast.statements().stream()
            .filter(statement -> !(statement instanceof DataDecl || statement instanceof Fun))
            .map(this::generateStatement)
            .filter(line -> !line.isBlank())
            .toList();
            
        mainStatements.forEach(line -> 
            mainCodeBuilder.append("        ").append(line).append("\n"));
    }

    private String buildFinalCode() {
        StringBuilder codeBuilder = new StringBuilder();
        
        generateImports(codeBuilder);
        generateClassHeader(codeBuilder);
        generateConstructorTypesSection(codeBuilder);
        generateMethodsSection(codeBuilder);
        generateExtraMethods(codeBuilder);
        generateMainMethodSection(codeBuilder);
        codeBuilder.append("}\n");
        
        return codeBuilder.toString();
    }

    private void generateImports(StringBuilder codeBuilder) {
        if (!imports.isEmpty()) {
            imports.forEach(imp -> codeBuilder.append("import ").append(imp).append(";\n"));
            codeBuilder.append("\n");
        }
    }

    private void generateClassHeader(StringBuilder codeBuilder) {
        codeBuilder.append("public class ").append(className).append(" {\n\n");
    }

    private void generateConstructorTypesSection(StringBuilder codeBuilder) {
        if (constructorTypes.length() > 0) {
            codeBuilder.append(constructorTypes);
        }
    }

    private void generateMethodsSection(StringBuilder codeBuilder) {
        if (methodDefinitions.length() > 0) {
            codeBuilder.append(methodDefinitions).append("\n");
        }
    }

    private void generateExtraMethods(StringBuilder codeBuilder) {
        if (extraMethods.contains("pow")) {
            codeBuilder.append("    public static int pow(int x, int e) {\n");
            codeBuilder.append("        return (int)Math.pow(x, e);\n");
            codeBuilder.append("    }\n\n");
        }
        if (extraMethods.contains("print")) {
            codeBuilder.append("    public static void print(Object arg) {\n");
            codeBuilder.append("        System.out.println(arg);\n");
            codeBuilder.append("    }\n\n");
        }
    }

    private void generateMainMethodSection(StringBuilder codeBuilder) {
        codeBuilder.append("    public static void main(String... args) {\n");
        codeBuilder.append(mainCodeBuilder);
        codeBuilder.append("    }\n");
    }

    private String generateStatement(Node stat) {
        return switch (stat) {

            case Let(var id, var value, var typeNode) -> {
                String valueCode = generateExpression(value);
                String varType = typeNode != null ? 
                    generateType(typeNode) : inferTypeFromValue(value);
                yield varType + " " + generateExpression(id) + " = " + valueCode + ";";
            }

            case Print(var expr) -> {
                extraMethods.add("print");
                yield "print(" + generateExpression(expr) + ");";
            }

            case Fun(var name, var params, var returnType, var body) -> {
                String paramDecls = params.stream()
                    .map(param -> {
                        String paramType = generateType(param.type());
                        return paramType + " " + generateExpression(param.id());
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

                String returnTypeJava = generateType(returnType);
                String bodyCode = generateExpression(body);

                if (body instanceof Match) {
                    switch (returnTypeJava) {
                        case "int" -> bodyCode = "((Integer)" + bodyCode + ")";
                        case "float" -> bodyCode = "((Float)" + bodyCode + ")";
                        case "boolean" -> bodyCode = "((Boolean)" + bodyCode + ")";
                        default -> { }
                    }
                }

                String methodDef =
                    "    public static " + returnTypeJava + " " + generateExpression(name) + "(" + paramDecls + ") {\n" +
                    "        return " + bodyCode + ";\n" +
                    "    }\n";

                methodDefinitions.append(methodDef);
                yield "";
            }

            case DataDecl(String _, List<DataDecl.Constructor> _) -> "";

            default -> "";
        };
    }


    private String generateExpression(Node expr) {
        return switch (expr) {

            case IntLiteral(var value) -> Integer.toString(value);
            case FloatLiteral(var value) -> value + "f";
            case BooleanLiteral(var value) -> Boolean.toString(value);
            case StringLiteral(var value) -> "\"" + escapeString(value) + "\"";
            case NoneLiteral _ -> "null";

            case Id(var value) -> value;

            case Pow(var left, var right) -> {
                extraMethods.add("pow");
                yield "pow(" + autoUnbox(generateExpression(left)) + ", " + autoUnbox(generateExpression(right)) + ")";
            }

            case MultDiv(var left, var op, var right) ->
                autoUnbox(generateExpression(left)) + " " + op + " " + autoUnbox(generateExpression(right));

            case AddSub(var left, var op, var right) ->
                autoUnbox(generateExpression(left)) + " " + op + " " + autoUnbox(generateExpression(right));

            case UnaryOp(var op, var expr2) ->
                op + "(" + autoUnbox(generateExpression(expr2)) + ")";

            case PostOp(var expr1, var op) ->
                autoUnbox(generateExpression(expr1)) + op;

            case Paren(var value) ->
                "(" + generateExpression(value) + ")";

            case TernaryCondition(var condition, var value1, var value2) -> 
                "(" + autoUnbox(generateExpression(condition)) + " != 0 ? " 
                    + autoUnbox(generateExpression(value1)) + " : " 
                    + autoUnbox(generateExpression(value2)) + ")";

            case Lambda(var args, var body) -> {
                imports.add("java.util.function.*");
                String params = args.stream()
                    .map(Id::value)
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
                if (functionNames.contains(id.value())) 
                    yield id.value() + "(" + params + ")";
                else yield generateExpression(id) + ".apply(" + params + ")";
            }

            case ConstructorInvocation(var id, var args) -> {
                String capitalizedId = capitalizeFirst(id);
                String argCode = args.stream()
                    .map(this::generateExpression)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                yield "new " + capitalizedId + "(" + argCode + ")";
            }

            case Match(var scrut, var cases) -> generateMatch(scrut, cases);

            default -> throw new IllegalArgumentException("Expresión no soportada: " + expr.getClass().getSimpleName());
        };
    }

    private String autoUnbox(String expr) {
    if (expr == null || expr.isBlank()) return expr;
    if (expr.matches("-?\\d+(\\.\\d+)?") || expr.startsWith("\"") || expr.equals("null"))
        return expr;

    if (expr.contains("Math.pow") || expr.contains("pow(") || expr.contains("+") || expr.contains("-") || expr.contains("*") || expr.contains("/"))
        return expr;

    if (expr.contains("(") && expr.contains(")")) return expr;

    return "((Integer)" + expr + ")";
    }


    
    private String generateType(Node typeNode) {
        return switch (typeNode) {
            case TypeNode(var typeName) -> switch (typeName) {
                case "int" -> "int";
                case "float" -> "float";
                case "boolean" -> "boolean";
                case "string" -> "String";
                case "any" -> "Object";
                case "void" -> "void";
                default -> capitalizeFirst(typeName);
            };
            case ArrowType(var from, var to) -> {
                String fromType = generateType(from);
                String toType = generateType(to);
                yield "java.util.function.Function<" + fromType + ", " + toType + ">";
            }
            case TupleType(var _) -> "Object[]";
            default -> "Object";
        };
    }
    
    private String inferTypeFromValue(Node value) {
        return switch (value) {
            case ConstructorInvocation(var id, var args) -> capitalizeFirst(id);
            case IntLiteral _ -> "int";
            case FloatLiteral _ -> "float";
            case BooleanLiteral _ -> "boolean";
            case StringLiteral _ -> "String";
            case Lambda _ -> lambdaType(value);
            default -> "Object";
        };
    }
    
    private String lambdaType(Node expr) {
        imports.add("java.util.function.*");
        return switch (expr) {
            case Lambda l -> {
                if (l.args().isEmpty()) yield "Supplier<Object>";
                if (l.args().size() == 1) yield "Function<Object, Object>";
                yield "BiFunction<Object, Object, Object>";
            }
            default -> "Function<Object, Object>";
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

    private String generateMatch(Node scrutinee, List<Match.Case> cases) {
    imports.add("java.util.function.*");

    String sVar = freshTmp("scrut");

    String caseCode = IntStream.range(0, cases.size())
        .mapToObj(i -> {
            Match.Case c = cases.get(i);
            StringBuilder bindings = new StringBuilder();

            String cond = emitPatternTest(sVar, c.pattern(), bindings);
            String guard = (c.guard() != null) ? generateExpression(c.guard()) : null;
            String fullCond = (guard == null) ? cond : "(" + cond + ") && (" + guard + ")";
            String body = generateExpression(c.body());
            String prefix = (i == 0) ? "    if (" : "    else if (";

            return prefix + fullCond + ") {\n"
                + bindings
                + "        return " + autoCastIfNeeded(body, c.body()) + ";\n"
                + "    }\n";
        })
        .collect(Collectors.joining());

    return Stream.of(
            "((Supplier<Object>) () -> {",
            "    var " + sVar + " = " + generateExpression(scrutinee) + ";",
            caseCode,
            "    else { throw new RuntimeException(\"Match failure\"); }",
            "}).get()"
        )
        .collect(Collectors.joining("\n"));
    }


    private int tmpCounter = 0;
    private String freshTmp(String p){ return "__" + p + (tmpCounter++); }

    private String emitPatternTest(String base, Pattern pat, StringBuilder bindings) {
        if (pat instanceof WildcardPat) return "true";


        if (pat instanceof VarPat vp) {
            bindings.append("        var ").append(vp.name())
                    .append(" = ").append(base).append(";\n");
            return "true";
        }

        if (pat instanceof NativePat np) {
            Node v = np.value();

            if (v instanceof NoneLiteral) return base + " == null";
            if (v instanceof StringLiteral s) return base + ".equals(" + generateExpression(s) + ")";
            if (v instanceof BooleanLiteral || v instanceof IntLiteral || v instanceof FloatLiteral)
                return base + " instanceof " + getBoxedType(v)
                    + " && ((" + getBoxedType(v) + ")" + base + ") == " + generateExpression(v);
            return "Objects.equals(" + base + ", " + generateExpression(v) + ")";
        }

        if (pat instanceof DataPat dp) {
            String cname = capitalizeFirst(dp.id());
            String castVar = freshTmp("c");

            String subConds = IntStream.range(0, dp.args().size())
                .mapToObj(i -> {
                    var subpat = dp.args().get(i);
                    String fieldName = switch (dp.id()) {
                        case "Cons" -> (i == 0 ? "car" : "cdr");
                        default -> "arg" + i;
                    };
                    String fieldAccess = castVar + "." + fieldName + "()";
                    return emitPatternTest(fieldAccess, subpat, bindings);
                })
                .collect(Collectors.joining(" && "));

            return Stream.of(base + " instanceof " + cname + " " + castVar, subConds)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(" && "));
        }

        throw new IllegalArgumentException("Unsupported pattern: " + pat.getClass().getSimpleName());
    }

    private String getBoxedType(Node literal) {
    return switch (literal) {
        case IntLiteral _ -> "Integer";
        case FloatLiteral _ -> "Float";
        case BooleanLiteral _ -> "Boolean";
        default -> "Object";
        };
    }

    private String autoCastIfNeeded(String bodyCode, Node bodyExpr) {
    if (bodyExpr instanceof IntLiteral) return "((Integer)" + bodyCode + ")";
    if (bodyExpr instanceof FloatLiteral) return "((Float)" + bodyCode + ")";
    if (bodyExpr instanceof BooleanLiteral) return "((Boolean)" + bodyCode + ")";
    return bodyCode;
    }

}