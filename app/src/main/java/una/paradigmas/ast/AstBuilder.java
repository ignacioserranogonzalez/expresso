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
        return new IntLiteral(value);
    }

    @Override
    public Node visitFloat(FloatContext ctx) {
        float value = Float.parseFloat(ctx.FLOAT().getText());
        return new FloatLiteral(value);
    }

    @Override
    public Node visitId(IdContext ctx) {
        return new Id(ctx.ID().getText());
    }

    @Override
    public Node visitAddSub(AddSubContext ctx) {
        Node left = visit(ctx.expr(0));
        Node right = visit(ctx.expr(1)); 
        String op = ctx.PLUS() != null ? "+" : "-";
        return new AddSub(left, right, op);
    }

    @Override
    public Node visitMultDiv(MultDivContext ctx) {
        Node left = visit(ctx.expr(0));
        Node right = visit(ctx.expr(1)); 
        String op = ctx.MULT() != null ? "*" : "/";
        return new MultDiv(left, right, op);
    }

    @Override
    public Node visitPow(PowContext ctx) {
        Node left = visit(ctx.expr(0));
        Node right = visit(ctx.expr(1)); 
        return new Pow(left, right);
    }

    @Override
    public Node visitPostInc(PostIncContext ctx) {
        Node expr = visit(ctx.expr());
        String op = ctx.INC().getText();
        return new PostOp(expr, op);
    }

    @Override
    public Node visitPostDec(PostDecContext ctx) {
        Node expr = visit(ctx.expr());
        String op = ctx.DEC().getText();
        return new PostOp(expr, op);
    }

    @Override
    public Node visitUnaryOp(UnaryOpContext ctx) {
        String op = ctx.PLUS() != null ? "+" : "-";
        Node num = visit(ctx.num());
        return new UnaryOp(op, num);
    }

    @Override
    public Node visitParen(ParenContext ctx) {
        Node expr = visit(ctx.expr());
        return new Paren(expr);
    }

    @Override
    public Node visitCall(CallContext ctx) {
        String id = ctx.ID().getText();
        Node expr = visit(ctx.expr());
        return new Call(new Id(id), expr);
    }

    @Override
    public Node visitLambda(LambdaContext ctx) {
        String id = ctx.ID().getText();
        Node expr = visit(ctx.expr());
        return new Lambda(new Id(id), expr);
    }

    @Override
    public Node visitLetDecl(LetDeclContext ctx) {
        String id = ctx.ID().getText();
        Node value = visit(ctx.expr());
        return new Let(new Id(id), value);
    }

    @Override
    public Node visitPrint(PrintContext ctx) {
        Node expr = visit(ctx.expr());
        return new Print(expr);
    }

    @Override
    public Node visitBlank(BlankContext ctx) {
        return null;
    }
}