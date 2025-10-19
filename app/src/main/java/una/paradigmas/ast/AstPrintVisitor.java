package una.paradigmas.ast;

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

public class AstPrintVisitor implements Visitor<String> {

    @Override
    public String visitProgram(Program program) {
        String result = "Program(" + String.join(", ",
                program.statements().stream()
                        .map(statement -> statement.accept(this))
                        .toList()) + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitType(TypeNode type) {
        String result = "Type(" + type.typeName() + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitInt(IntLiteral num) {
        String result = "Int(" + num.value() + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitFloat(FloatLiteral num) {
        String result = "Float(" + num.value() + ")";
        System.out.println(result);
        return result;
    }

    
    @Override
    public String visitBoolean(BooleanLiteral num) {
        String result = "Boolean(" + num.value() + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitString(StringLiteral num) {
        String result = "String(" + num.value() + ")";
        System.out.println(result);
        return result;
    }
    
    @Override
    public String visitId(Id id) {
        String result = "Id(" + id.value() + ")";
        System.out.println(result);
        return result;
    }
    
    @Override
    public String visitPow(Pow pow) {
        String result = "Pow(" + pow.left().accept(this) + ", " +
                        pow.right().accept(this) + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitAddSub(AddSub addSub) {
        String result = "AddSub(" + addSub.left().accept(this) + ", " +
                        addSub.right().accept(this) + ", " + addSub.op() + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitMultDiv(MultDiv multDiv) {
        String result = "MultDiv(" + multDiv.left().accept(this) + ", " +
                        multDiv.right().accept(this) + ", " + multDiv.op() + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitUnaryOp(UnaryOp unOp) {
        String result = "UnaryOp(" + unOp.op() + ", " +
                        unOp.expr().accept(this) + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitPostOp(PostOp postOp) {
        String result = "PostOp(" + postOp.expr().accept(this) + ", " + postOp.op() + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitParen(Paren paren) {
        String result = "Paren(" + paren.expr().accept(this) + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitLet(Let let) {
        String result = "Let(" + let.id().accept(this) + ", " +
                        let.value().accept(this) + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitPrint(Print print) {
        String result = "Print(" + print.expr().accept(this) + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitLambda(Lambda lambda) {
        String args = lambda.args().stream()
                .map(arg -> arg.accept(this))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        String result = "Lambda([" + args + "], " + lambda.expr().accept(this) + ")";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitCall(Call call) {
        String args = call.args().stream()
                .map(arg -> arg.accept(this))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        String result = "Call(" + call.id().accept(this) + ", [" + args + "])";
        System.out.println(result);
        return result;
    }

    @Override
    public String visitTernaryCondition(TernaryCondition ternary) {
        String result = "TernaryCondition(" + ternary.condition().accept(this) + ", " +
                        ternary.value1().accept(this) + ", " +
                        ternary.value2().accept(this) + ")";
        System.out.println(result);
        return result;
    }
     @Override
    public String visitDataDecl(DataDecl dataDecl) {
        String constructors = dataDecl.constructors().stream()
            .map(constructor -> {
                String arguments = constructor.arguments().stream()
                    .map(arg -> {
                        String argName = arg.name().isEmpty() ? "unnamed" : arg.name();
                        String argType = arg.type().accept(this);
                        return "Argument(" + argName + ", " + argType + ")";
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
                
                return "Constructor(" + constructor.id() + ", [" + arguments + "])";
            })
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        
        String result = "DataDecl(" + dataDecl.id() + ", [" + constructors + "])";
        System.out.println(result);
        return result;
    }
}