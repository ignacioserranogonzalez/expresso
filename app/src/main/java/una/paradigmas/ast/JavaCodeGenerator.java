package una.paradigmas.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import una.paradigmas.node.*;

public class JavaCodeGenerator {

    private final Set<String> imports = new HashSet<>();
    private final String className;
    private final StringBuilder methodDefinitions = new StringBuilder();
    private final Set<String> extraMethods = new HashSet<>();
    private final StringBuilder constructorTypes = new StringBuilder();
    private final StringBuilder mainCodeBuilder = new StringBuilder();
    private final List<DataDecl> dataDeclarations = new ArrayList<>();
    private SymbolTable symbolTable;

    public JavaCodeGenerator(String className) {
        this.className = capitalizeFirst(className);
    }

    public String generate(Program ast) {
        resetBuilders();
        this.symbolTable = ast.symbolTable();

        extractDataDeclarations(ast);
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
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        
        constructorTypes.append("    sealed interface ").append(typeName)
            .append(" permits ").append(permits).append(" {}\n");
        
        constructors.forEach(constructor -> {
            String constructorName = capitalizeFirst(constructor.id());
            
            if (constructor.arguments().isEmpty()) {
                constructorTypes.append("    record ").append(constructorName)
                    .append("() implements ").append(typeName).append(" {}\n");
            } else {
                String argParams = constructor.arguments().stream()
                    .map(arg -> {
                        String argType = symbolTable.getType(arg.name());
                        String argName = arg.name().isEmpty() 
                            ? "arg" + arg.hashCode() % 100
                            : arg.name();
                        return (argType != null ? argType : "Object") + " " + argName;
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                
                constructorTypes.append("    record ").append(constructorName)
                    .append("(").append(argParams).append(") implements ")
                    .append(typeName).append(" {}\n");
            }
        });
        
        constructorTypes.append("\n");
    }

    private void generateMethodDefinitions(Program ast) {
        List<String> methodStatements = ast.statements().stream()
            .filter(statement -> statement instanceof Fun)
            .map(statement -> generateStatement(statement))
            .filter(line -> !line.isBlank())
            .toList();
            
        methodStatements.forEach(line -> 
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
        if (extraMethods.contains("printAndReturnNull")) {
            codeBuilder.append("    public static Object printAndReturnNull(Object arg) {\n");
            codeBuilder.append("        System.out.println(arg);\n");
            codeBuilder.append("        return null;\n");
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
                String varType = symbolTable.getType(id.value());
                yield (varType != null ? varType : "Object") + " " + generateExpression(id) + " = " + valueCode + ";";
            }                                 

            case Print(var expr) -> {
                extraMethods.add("print");
                yield "print(" + generateExpression(expr) + ");";
            }

            case PrintExpr(var expr) -> {
                extraMethods.add("printAndReturnNull");
                yield "printAndReturnNull(" + generateExpression(expr) + ");";
            }

            case Fun(var name, var params, var returnType, var body) -> {
                String paramDecls = params.stream()
                    .map(param -> {
                        String paramType = symbolTable.getType(param.id().value());
                        return (paramType != null ? paramType : "Object") + " " + generateExpression(param.id());
                    })
                    .collect(Collectors.joining(", "));
                
                String returnTypeJava = symbolTable.getType(name.value());
                String bodyCode = generateExpression(body);
                
                String methodDef = "public static " + (returnTypeJava != null ? returnTypeJava : "Object") + " " + 
                    generateExpression(name) + "(" + paramDecls + ") {\n" +
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

            case Id(var value) -> value;

            case Pow(var left, var right) -> {
                extraMethods.add("pow");
                yield "pow(" + generateExpression(left) + ", " + generateExpression(right) + ")";
            }

            case MultDiv(var left, var op, var right) ->
                generateExpression(left) + " " + op + " " + generateExpression(right);

            case AddSub(var left, var op, var right) ->
                generateExpression(left) + " " + op + " " + generateExpression(right);

            case UnaryOp(var op, var expr2) ->
                op + "(" + generateExpression(expr2) + ")";

            case PostOp(var expr1, var op) ->
                generateExpression(expr1) + op;

            case Paren(var value) ->
                "(" + generateExpression(value) + ")";

            case RelOp(var left, var op, var right) ->
                "(" + generateExpression(left) + " " + op + " " + generateExpression(right) + ")";

            case LogicalOp(var left, var op, var right) ->
                "(" + generateExpression(left) + " " + op + " " + generateExpression(right) + ")";

            case NotOp(var expr3) ->
                "!" + generateExpression(expr3);

            case TernaryCondition(var condition, var value1, var value2) -> {
                String conditionExpr = switch (condition) {
                    case Id id -> {
                        String type = symbolTable.getType(id.value());
                        if ("boolean".equals(type)) {
                            yield generateExpression(condition);
                        } else {
                            yield generateExpression(condition) + " != 0";
                        }
                    }
                    case BooleanLiteral _ -> generateExpression(condition);
                    default -> generateExpression(condition) + " != 0";
                };
                
                yield "(" + conditionExpr + " ? " + generateExpression(value1) 
                    + " : " + generateExpression(value2) + ")";
            }

            case Lambda(var args, var body) -> {
                imports.add("java.util.function.*");
                String params = args.stream()
                    .map(id -> {
                        String type = symbolTable.getType(id.value());
                        return (type != null ? type : "Object") + " " + id.value();
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .map(s -> args.size() == 1 ? s : "(" + s + ")")
                    .orElse("()");
                
                String lambdaType = symbolTable.getType(((Lambda) expr).toString());
                if (lambdaType != null && lambdaType.startsWith("Function")) {
                    generateFunctionInterface(args.size());
                }
                
                yield params + " -> " + generateExpression(body);
            }

            case Call(var id, var paramList) -> {
                String params = paramList.stream()
                    .map(this::generateExpression)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                
                if (symbolTable.isConstructor(id.value())) {
                    yield "new " + capitalizeFirst(id.value()) + "(" + params + ")";
                } else if (symbolTable.isMethod(id.value())) {
                    yield id.value() + "(" + params + ")";
                } else {
                    yield generateExpression(id) + ".apply(" + params + ")";
                }
            }

            case ConstructorInvocation(var id, var args) -> {
                String capitalizedId = capitalizeFirst(id);
                String argCode = args.stream()
                    .map(this::generateExpression)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                yield "new " + capitalizedId + "(" + argCode + ")";
            }

            case Match(var exprToMatch, var rules) -> {
                String exprCode = generateExpression(exprToMatch);
                StringBuilder sb = new StringBuilder();
                sb.append("switch (").append(exprCode).append(") {\n");
                for (Node ruleNode : rules) {
                    MatchRule rule = (MatchRule) ruleNode;
                    sb.append("            ").append(generateMatchRule(rule)).append("\n");
                }
                sb.append("        }");
                yield sb.toString();
            }

            case Cast(var expr4, var typeNode) -> {
                String exprCode = generateExpression(expr4);
                String javaType = symbolTable.getType(expr4.toString()) != null 
                    ? symbolTable.getType(expr4.toString()) 
                    : generateType(typeNode);
                yield "(" + javaType + ")" + exprCode;
            }

            case PrintExpr(Node innerExpr) -> {
                extraMethods.add("printAndReturnNull");
                String exprCode = generateExpression(innerExpr);
                yield "printAndReturnNull(" + exprCode + ")";
            }

            case NoneLiteral() -> "null"; 

            default -> throw new IllegalArgumentException("Expresión no soportada: " + expr.getClass().getSimpleName());
        };
    }

    private String generateMatchRule(MatchRule rule) {
        String bodyCode = generateExpression(rule.body());
        String guardCode = rule.guard() != null ? 
            " when " + generateExpression(rule.guard()) : "";
        
        return switch (rule.pattern()) {
            case DataPattern dp -> {
                String patternName = capitalizeFirst(dp.name());
                if (dp.subPatterns().isEmpty()) {
                    String varName = patternName.toLowerCase() + "_var";
                    yield "case " + patternName + " " + varName + guardCode + " -> " + bodyCode + ";";
                } else {
                    String vars = dp.subPatterns().stream()
                        .map(p -> p instanceof VariablePattern vp ? "var " + vp.name() : "var _")
                        .collect(Collectors.joining(", "));
                    yield "case " + patternName + "(" + vars + ")" + guardCode + " -> " + bodyCode + ";";
                }
            }
            case VariablePattern vp -> 
                "case var " + vp.name() + guardCode + " -> " + bodyCode + ";";
            case WildcardPattern _ -> 
                "default" + guardCode + " -> " + bodyCode + ";";
            case IntPattern ip -> 
                "case " + ip.value() + guardCode + " -> " + bodyCode + ";";
            case StringPattern sp -> 
                "case \"" + escapeString(sp.value()) + "\"" + guardCode + " -> " + bodyCode + ";";
            case BooleanPattern bp -> 
                "case " + bp.value() + guardCode + " -> " + bodyCode + ";";
            case NonePattern _ -> 
                "case null" + guardCode + " -> " + bodyCode + ";";
            default -> throw new IllegalArgumentException("Patrón no soportado: " + rule.pattern().getClass().getSimpleName());
        };
    }
    
    private String generateType(Node typeNode) {
        if (typeNode instanceof TypeNode(var typeName)) {
            return switch (typeName) {
                case "int" -> "int";
                case "float" -> "float";
                case "boolean" -> "boolean";
                case "string" -> "String";
                case "any" -> "Object";
                case "void" -> "void";
                default -> capitalizeFirst(typeName);
            };
        }
        return "Object";
    }

    private void generateFunctionInterface(int paramCount) {
        String interfaceName = "Function" + paramCount;
        
        if (methodDefinitions.toString().contains("interface " + interfaceName)) return;
        
        String typeParams = java.util.stream.IntStream.rangeClosed(1, paramCount)
            .mapToObj(i -> "T" + i)
            .collect(Collectors.joining(", ", "", ", R"));
        
        String applyParams = java.util.stream.IntStream.rangeClosed(1, paramCount)
            .mapToObj(i -> "T" + i + " arg" + i)
            .collect(Collectors.joining(", "));
        
        String interfaceDef = """
            @FunctionalInterface
            interface %s<%s> {
                R apply(%s);
            }
            """.formatted(interfaceName, typeParams, applyParams);
        
        String indentedDef = interfaceDef.lines()
            .map(line -> "    " + line)
            .collect(Collectors.joining("\n"));
        
        methodDefinitions.insert(0, indentedDef + "\n");
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