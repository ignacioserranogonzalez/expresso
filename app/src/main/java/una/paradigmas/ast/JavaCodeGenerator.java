package una.paradigmas.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    //------------------------------------------

    private String generateStatement(Node stat) {
        return switch (stat) {
            case Let(var id, var value, var typeNode) -> {
                String valueCode = generateExpression(value);
                String varType = switch (value) {
                    case Lambda _ -> lambdaType(value, typeNode);
                    default -> typeNode != null ? generateType(typeNode) : inferTypeFromValue(value);
                };
                yield varType + " " + generateExpression(id) + " = " + valueCode + ";";
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
                // parametros con tipos
                String paramDecls = params.stream()
                    .map(param -> {
                        String paramType = generateType(param.type());
                        return paramType + " " + generateExpression(param.id());
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                
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

            case TupleLiteral(var elements) -> {
                String elementsCode = elements.stream()
                    .map(this::generateExpression)
                    .collect(Collectors.joining(", "));
                
                // tipo de tupla: num de elementos
                if (elements.size() == 2) {
                    imports.add("java.util.Map");
                    yield "Map.entry(" + elementsCode + ")";
                } else yield "new Object[]{" + elementsCode + "}";
            }

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

            case Call(var id, var paramList) -> {
                String params = paramList.stream()
                    .map(this::generateExpression)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                
                    if (symbolTable.isConstructor(id.value())) {
                        yield "new " + capitalizeFirst(id.value()) + "(" + params + ")";
                    }
                    else if (symbolTable.getFunctionNames().contains(id.value())) {
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
            case NoneLiteral() -> "null"; 

             case PrintExpr(Node innerExpr) -> {
            extraMethods.add("printAndReturnNull");
            String exprCode = generateExpression(innerExpr);
            yield "printAndReturnNull(" + exprCode + ")";
        }
            

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
                case "float" -> "float";
                case "boolean" -> "boolean";
                case "string" -> "String";
                case "any" -> "Object";
                case "void" -> "void";
                default -> capitalizeFirst(typeName);
            };
            default -> "Object";
        };
    }
    
    private String inferTypeFromValue(Node value) {
        return switch (value) {
            case IntLiteral _ -> "int";
            case FloatLiteral _ -> "float";
            case BooleanLiteral _ -> "boolean";
            case StringLiteral _ -> "String";
            case Lambda _ -> lambdaType(value, null);
            default -> "Object";
        };
    }
    
    private String lambdaType(Node expr, Node explicitType) {
    imports.add("java.util.function.*");

    if (explicitType instanceof ArrowType arrowType) 
        return arrowType(arrowType);
    
        return switch (expr) {
            case Lambda l -> {
                int argCount = l.args().size();
                if (argCount <= 2) {
                    yield switch (argCount) {
                        case 0 -> "Supplier<Object>";
                        case 1 -> "Function<Object, Object>";
                        case 2 -> "BiFunction<Object, Object, Object>";
                        default -> "Object";
                    };
                } else {
                    generateFunctionInterface(argCount);
                    String typeParams = Collections.nCopies(argCount + 1, "Object")
                        .stream().collect(Collectors.joining(", "));
                    yield "Function" + argCount + "<" + typeParams + ">";
                }
            }
            default -> "Object";
        };
    }

    private String toWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case "int" -> "Integer";
            case "float" -> "Float"; 
            case "boolean" -> "Boolean";
            default -> primitiveType; // String, Object, etc
        };
    }

    private String arrowType(ArrowType arrow) {
        
        return switch (arrow.from()) {
            case TypeNode fromType -> {
                String from = toWrapperType(generateType(fromType));
                String to = toWrapperType(generateType(arrow.to()));
                
                if (from.equals("void")) 
                    yield "Supplier<" + to + ">";
                else 
                    yield "Function<" + from + ", " + to + ">";
            }
            
            case TupleType tuple -> {
                List<String> paramTypes = tuple.types().stream()
                    .map(this::generateType)
                    .map(this::toWrapperType)
                    .collect(Collectors.toList());
                String returnType = toWrapperType(generateType(arrow.to()));
                
                yield switch (paramTypes.size()) {
                    case 0 -> "Supplier<" + returnType + ">";
                    case 1 -> "Function<" + paramTypes.get(0) + ", " + returnType + ">";
                    case 2 -> "BiFunction<" + paramTypes.get(0) + ", " + paramTypes.get(1) + ", " + returnType + ">";
                    default -> {
                        String customName = "Function" + paramTypes.size();
                        generateFunctionInterface(paramTypes.size());
                        yield customName + "<" + String.join(", ", paramTypes) + ", " + returnType + ">";
                    }
                };
            }
            
            default -> "Function<Object, Object>";
        };
    }

    private void generateFunctionInterface(int paramCount) {
        String interfaceName = "Function" + paramCount;
        if (methodDefinitions.toString().contains("interface " + interfaceName)) return;
        
        List<String> typeParams = new ArrayList<>();
        for (int i = 0; i < paramCount; i++) {
            typeParams.add("T" + (i + 1));
        }
        typeParams.add("R");
        
        String applyParams = typeParams.stream()
            .limit(paramCount)
            .map(t -> t + " arg" + (typeParams.indexOf(t) + 1))
            .collect(Collectors.joining(", "));
        
        String interfaceDef = """
        @FunctionalInterface
        interface %s<%s> {
            R apply(%s);
        }
        """.formatted(interfaceName, String.join(", ", typeParams), applyParams);
        
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