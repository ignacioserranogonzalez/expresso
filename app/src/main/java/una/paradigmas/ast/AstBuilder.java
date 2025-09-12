package una.paradigmas.ast;

import una.paradigmas.ast.ExpressoParser.*;

import java.util.List;
import java.util.stream.Collectors;

public class AstBuilder extends ExpressoBaseVisitor<Expr> {

    @Override
    public Expr visitProgram(ProgramContext ctx) {
        List<Expr> statements = ctx.stat().stream().map(this::visit).filter(expr -> expr != null)
                                    .collect(Collectors.toList());
        return new Program(statements);
    }

    @Override
    public Expr visitExpression(ExpressionContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Expr visitInt(IntContext ctx) {
        int value = Integer.parseInt(ctx.INT().getText());
        return new Num(value);
    }

    @Override
    public Expr visitBinaryOp(BinaryOpContext ctx) {
        String op = ctx.PLUS() != null ? "+" : "-";
        Expr left = visit(ctx.expr(0));
        Expr right = visit(ctx.expr(1)); 
        return new BinaryOp(op, left, right);
    }

    @Override
    public Expr visitBlank(BlankContext ctx) {
        return null;
    }
}