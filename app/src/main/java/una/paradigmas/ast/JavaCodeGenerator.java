package una.paradigmas.ast;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaCodeGenerator implements Visitor<String> {

    private final Set<String> imports = new HashSet<>(); 
    private final Set<String> extraMethods = new HashSet<>(); 
    private final String className;

    public JavaCodeGenerator(String className) {
        this.className = className.toUpperCase().charAt(0)+ className.substring(1); 
    }
    
    public String visit (Program ast){

      
     List<String> statlist = ast.statements().stream()
                             .map(s -> s.accept(this)) 
                             .toList();                
    /*
     llama al metodo accept de program, este contiene 
     public <T> T accept(Visitor<T> visitor) {
        return visitor.visitProgram(this);
    }

    donde Visitor<T> visitor es JavaCodeGenerator
    entonces retorna javaCodeGenerator.visitProgram(this), lo cual llama al metodo visitProgram de JavaCodeGenerator
    */

    StringBuilder codeBuilder = new StringBuilder();

    codeBuilder.append("// ").append(className).append(".java\n");

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

   statlist.forEach(line -> codeBuilder.append("        ").append(line).append(";\n")); //se puede cambiar, mas FP y DRY

    codeBuilder.append("    }\n");
    codeBuilder.append("}\n");
    return codeBuilder.toString(); 
 
    }

    @Override
    public String visitProgram (Program program){
        return "";
    }

    @Override
    public String visitInt (IntLiteral num){
        return Integer.toString(num.value());
    }

    @Override
    public String visitFloat (FloatLiteral num){
        return String.valueOf(num.value());
    }

    @Override
    public String visitId (Id id){
        return id.value();
    }

    @Override
    public String visitPow (Pow pow){
        extraMethods.add("pow");
        return "pow(" + pow.left().accept(this) + ", " + pow.right().accept(this) + ")";
    }
    @Override
    public String visitMultDiv (MultDiv multDiv){
        return multDiv.left().accept(this) + " " + multDiv.op() + " " + multDiv.right().accept(this); 
    }

    @Override
    public String visitAddSub (AddSub addSub){
        return addSub.left().accept(this) + " " + addSub.op() + " " + addSub.right().accept(this) ; 
    }
    @Override
    public String visitUnaryOp(UnaryOp unaryOp){
        return unaryOp.op() + unaryOp.num().accept(this);
    }
    @Override
    public String visitPostOp(PostOp postOp){
        return postOp.expr().accept(this) + postOp.op();
    }
    @Override
    public String visitParen(Paren paren){
        return  paren.expr().accept(this);
    }
   @Override
    public String visitLambda(Lambda lambda) {
    imports.add("java.util.function.UnaryOperator"); 
    String param = lambda.id().accept(this);
    String body = lambda.expr().accept(this);
    return param + " -> " + body; 
    }

    @Override
    public String visitCall(Call call) {
        return call.id().accept(this) + ".apply(" + call.expr().accept(this) + ")";
    }

    @Override
    public String visitPrint(Print print) {
        extraMethods.add("print");
        return "print(" + print.expr().accept(this) + ")";
    }

    @Override
    public String visitLet(Let let) {
    String valueCode = let.value().accept(this);
    String varType = (let.value() instanceof Lambda) ? "UnaryOperator<Integer>" : "int"; //puede cambiarse??
    return varType + " " + let.id().accept(this) + " = " + valueCode;
}


}
