package una.paradigmas.ast;

import una.paradigmas.ast.ExpressoParser.*;

import java.util.ArrayList;
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
    public Node visitId(IdContext ctx) {
        return new Id(ctx.ID().getText());
    }

    @Override
    public Node visitAddSub(AddSubContext ctx) {
        Node left = visit(ctx.expr(0));
        Node right = visit(ctx.expr(1)); 
        String op = ctx.PLUS() != null ? "+" : "-";
        return new AddSub(left, op, right);
    }

    @Override
    public Node visitMultDiv(MultDivContext ctx) {
        Node left = visit(ctx.expr(0));
        Node right = visit(ctx.expr(1)); 
        String op = ctx.MULT() != null ? "*" : "/";
        return new MultDiv(left, op, right);
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
        // Obtener los args desde lambdaParams
        List<Id> args = new ArrayList<>();
        
        // Dependiendo de cómo se definió lambdaArgs, necesitas acceder a sus hijos
        // Si lambdaArgs tiene IDs, los obtenemos así:
        if (ctx.lambdaParams().ID() != null) {
            args = ctx.lambdaParams().ID().stream()
                .map(idNode -> new Id(idNode.getText()))
                .collect(Collectors.toList());
        }
        // Si no hay IDs (caso '()'), args queda como lista vacía
        
        Node expr = visit(ctx.expr());
        return new Lambda(args, expr);
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

    @Override
    public Node visitComment(CommentContext ctx) {
    return new Comment(ctx.COMMENT().getText());
    }

    @Override
    public Node visitMultilineComment(MultilineCommentContext ctx) {
    return new Comment(ctx.MULTILINE_COMMENT().getText());
    }

    @Override
    public Node visitTernaryCondition(TernaryConditionContext ctx) {
    return new TernaryCondition(visit(ctx.expr(0)), visit(ctx.expr(1)), visit(ctx.expr(2)));
    }
}