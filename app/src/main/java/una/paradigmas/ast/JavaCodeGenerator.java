package una.paradigmas.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import una.paradigmas.node.*;

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
    private final List<DataDecl> dataDeclarations = new ArrayList<>(); // revisar
    private SymbolTable symbolTable = new SymbolTable();

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

        // System.out.println("\n[symbolTable JavaCodeGenerator]\n" + symbolTable);
        
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
                        String argType = generateType(arg.type());
                        String argName = arg.name().isEmpty() 
                            ? "arg" + arg.hashCode() % 100
                            : arg.name();
                        return argType + " " + argName;
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
        String methodTemplate = """
                public static %s %s(%s) {
            %s
                }
        
            """;
        
        if (extraMethods.contains("pow")) {
            codeBuilder.append(methodTemplate.formatted(
                "double",             
                "pow",                
                "double x, double e",  
                "        return Math.pow(x, e);" 
            ));
        }
    
        if (extraMethods.contains("print")) {
            codeBuilder.append(methodTemplate.formatted(
                "Object",
                "print",
                "Object arg",
                "        System.out.println(arg);\n        return null;"
            ));
        }
    }
    private void generateMainMethodSection(StringBuilder codeBuilder) {
        codeBuilder.append("""
                public static void main(String... args) {
            %s
                }
            
            """.formatted(mainCodeBuilder.toString()));
    }

    //------------------------------------------

    private String generateStatement(Node stat) {
        return switch (stat) {
            case Let(var id, var value, var typeNode) -> {
                String valueCode = generateExpression(value);
                String varType = symbolTable.getType(id.value());
                
                if (varType == null) {
                    varType = typeNode != null ? generateType(typeNode) : "Object";
                }
                
                yield switch (value) {
                    case Lambda lambda -> { // para las lambdas
                        String functionType = symbolTable.getFunctionType(id.value());
                        int paramCount = lambda.params().size();
                        if(paramCount > 2) generateFunctionInterface(paramCount);
                        yield functionType + " " + generateExpression(id) + " = " + valueCode + ";";
                    }
                    default -> varType + " " + generateExpression(id) + " = " + valueCode + ";";
                };
            }                            

            case Fun(var name, var params, var returnType, var body) -> {
                // parametros con tipos
                String paramDecls = params.stream()
                .map(param -> {
                    String paramType = generateType(param.type());
                    return paramType + " " + generateExpression(param.id());
                })
                .collect(Collectors.joining(", "));
                
                String returnTypeJava = generateType(returnType);
                String bodyCode = generateExpression(body);
                
                String methodDef = "    public static " + returnTypeJava + " " + 
                    generateExpression(name) + "(" + paramDecls + ") {\n" +
                    "        return " + bodyCode + ";\n" +
                    "    }\n";
                
                methodDefinitions.append(methodDef);
                yield "";
            }

            case DataDecl(String _, List<DataDecl.Constructor> _) -> "";

            case Call(var callee, var paramList) -> {
                var params = paramList.stream()
                    .map(this::generateExpression)
                    .collect(Collectors.joining(", "));
            
                yield generateCall(callee, params) + ";";
            }

            default -> generateExpression(stat);
        };
    }

    private String generateExpression(Node expr) {
        return switch (expr) {
            case IntLiteral(var value) -> Integer.toString(value);
            case DoubleLiteral(var value) -> Double.toString(value);
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
                String conditionExpr = Optional.of(condition)
                    // .filter(this::isBooleanExpression)
                    .map(this::generateExpression)
                    .orElse(generateExpression(condition) + " != 0");
                
                yield "(" + conditionExpr + " ? " + generateExpression(value1) 
                    + " : " + generateExpression(value2) + ")";
            }

            case Lambda(var _, var params, var _, var body) -> {
                imports.add("java.util.function.*");
                
                String args = params.stream()
                    .map(param -> {
                        String paramName = generateExpression(param.id());
                        return paramName;
                    })
                    .collect(Collectors.joining(", "));
                
                String bodyCode = generateExpression(body);
                
                yield (params.size() == 1 ? args : "(" + args + ")") + " -> " + bodyCode;
            }

            case Call(var callee, var paramList) -> {
                String params = paramList.stream()
                    .map(this::generateExpression)
                    .collect(Collectors.joining(", "));
            
                yield generateCall(callee, params);
            }

            case Print(var printExpr) -> {
                extraMethods.add("print");
                yield "print(" + generateExpression(printExpr) + ");";
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
                String javaType = generateType(typeNode);
                yield "(" + javaType + ")" + exprCode;
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
        return switch (typeNode) {
            case TypeNode(var typeName) -> switch (typeName) {
                case "int" -> "int";
                case "double" -> "double";
                case "boolean" -> "boolean";
                case "string" -> "String";
                case "any" -> "Object";
                case "void" -> "void";
                default -> capitalizeFirst(typeName);
            };
            default -> "Object";
        };
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

    private String generateCall(Node callee, String params){
        return switch (callee) {

            case Id id when symbolTable.isLambda(id.value()) -> {
                String lambdaType = symbolTable.getFunctionType(id.value());
                
                yield switch (lambdaType) {
                    case String lt when lt.startsWith("Supplier") -> 
                        id.value() + ".get()";
                        
                    case String lt when lt.startsWith("Consumer") -> 
                        id.value() + ".accept(" + params + ")";
                        
                    case String lt when lt.startsWith("Function") -> 
                        id.value() + ".apply(" + params + ")";
                        
                    case String lt when lt.startsWith("BiConsumer") -> 
                        id.value() + ".accept(" + params + ")";
                        
                    case String lt when lt.startsWith("BiFunction") -> 
                        id.value() + ".apply(" + params + ")";

                    case String lt when lt.startsWith("BiPredicate") -> 
                        id.value() + ".test(" + params + ")";
                        
                    case String lt when lt.startsWith("Function") && lt.contains("Function") -> 
                        id.value() + ".apply(" + params + ")";
                        
                    default -> 
                        id.value() + ".apply(" + params + ")";
                };
            }

            case Id id -> {
                yield symbolTable.isConstructor(id.value()) ? 
                    "new " + capitalizeFirst(id.value()) + "(" + params + ")" 
                    : symbolTable.getMethodNames().contains(id.value()) ?
                        id.value() + "(" + params + ")" 
                        : id.value() + ".apply(" + params + ")";
            }
    
            case Call _ -> {
                // generar recursivamente el callee ( .apply().apply() )
                var calleeCode = generateExpression(callee);
                yield calleeCode + ".apply(" + params + ")";
            }
    
            default -> generateExpression(callee) + ".apply(" + params + ")";
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