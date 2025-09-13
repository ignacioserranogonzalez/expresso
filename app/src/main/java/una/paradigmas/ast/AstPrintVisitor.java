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
    public String visitNum(Num num) {
        String result = "Num(" + num.value() + ")";
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
}
