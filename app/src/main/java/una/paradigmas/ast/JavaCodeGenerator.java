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
        List<String> statlist = ast.statements().stream()
                             .map(this::generateStatement)
                             .toList();

        StringBuilder codeBuilder = new StringBuilder();
        
        // agrega los comentarios al inicio del codigo
        statlist.stream()
                .takeWhile(line -> line.startsWith("//"))
                .forEachOrdered(line -> codeBuilder.append(line).append("\n"));
        
        if(imports.contains("java.util.function.UnaryOperator")){
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

        //se encarga de los comentarios y las instrucciones restantes 
        statlist.stream()
                .dropWhile(line -> line.startsWith("//"))
                .forEachOrdered(line -> 
                    codeBuilder.append("        ")
                               .append(line.startsWith("//") ? line : line + ";")
                               .append("\n")
                );

        codeBuilder.append("    }\n");
        codeBuilder.append("}\n");
        return codeBuilder.toString(); 
    }

    private String generateStatement(Node stat) {
        return switch (stat) {
            case Let(var id, var value) -> {
                String valueCode = generateExpression(value);
                String varType = (value instanceof Lambda) ? "UnaryOperator<Integer>" : "int";
                // se uso yield para producir un valor (a pesar de que no se ha visto en clase)
                // porque es un block case {} y no se puede usar return de nuevo
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
            case Lambda(var id, var expr1) -> {
                imports.add("java.util.function.UnaryOperator");
                yield generateExpression(id) + " -> " + generateExpression(expr1);
            }
            case Call(var id, var expr1) -> 
                generateExpression(id) + ".apply(" + generateExpression(expr1) + ")";
            default -> "";
        };
    }
    
}