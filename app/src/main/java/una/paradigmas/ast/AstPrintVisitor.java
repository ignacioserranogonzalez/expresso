package una.paradigmas.ast;

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
    public String visitId(Id id) {
        String result = "Id(" + id.value() + ")";
        System.out.println(result);
        return result;
    }
    
    @Override
    public String visitBinaryOp(BinaryOp binOp) {
        String result = "BinaryOp(" + binOp.op() + ", "
        + binOp.left().accept(this) + ", "
        + binOp.right().accept(this) + ")";
        System.out.println(result);
        return result;
    }
    
    @Override
    public String visitLet(Let let) {
        String result = "Let(" + let.id() + ", " 
                        + let.value().accept(this) + ")";
        System.out.println(result);
        return result;
    }
}
