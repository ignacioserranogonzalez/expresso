package una.paradigmas.ast;

import una.paradigmas.ast.ExpressoParser.*;

import java.util.List;
import java.util.stream.Collectors;

public class AstBuilder extends ExpressoBaseVisitor<Node> {

    @Override
    public Program visitProgram(ProgramContext ctx) {
        List<Node> statements = ctx.stat().stream().map(this::visit).filter(expr -> expr != null)
                                    .collect(Collectors.toList());
        return new Program(statements);
    }

    @Override
    public Node visitExpression(ExpressionContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Node visitInt(IntContext ctx) {
        int value = Integer.parseInt(ctx.INT().getText());
        return new Num(value);
    }

    @Override
    public Node visitBinaryOp(BinaryOpContext ctx) {
        String op = ctx.PLUS() != null ? "+" : "-";
        Node left = visit(ctx.expr(0));
        Node right = visit(ctx.expr(1)); 
        return new BinaryOp(op, left, right);
    }

    @Override
    public Node visitBlank(BlankContext ctx) {
        return null;
    }
}