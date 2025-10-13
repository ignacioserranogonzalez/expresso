package una.paradigmas.ast;

import una.paradigmas.ast.ExpressoParser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
 * Nota: Este codigo tiene adiciones de IA para cumplir 
 * con los requerimientos especificos del proyecto.
 */

public class AstBuilder extends ExpressoBaseVisitor<Node> {

    @Override
    public Program visitProgram(ProgramContext ctx) {
        List<Node> statements = ctx.stat().stream()
                .map(this::visit)
                .filter(expr -> expr != null)
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
    public Node visitFloat(ExpressoParser.FloatContext ctx) {
        double value = Double.parseDouble(ctx.FLOAT().getText());
        return new FloatLiteral(value);
    }

    @Override
    public Node visitBoolean(ExpressoParser.BooleanContext ctx) {
        boolean value = Boolean.parseBoolean(ctx.BOOLEAN().getText());
        return new BooleanLiteral(value);
    }

    @Override
    public Node visitString(ExpressoParser.StringContext ctx) {
        String text = ctx.STRING().getText();
        // Remover comillas y procesar escapes
        String value = text.substring(1, text.length() - 1)
                          .replace("\\\"", "\"")
                          .replace("\\\\", "\\")
                          .replace("\\n", "\n")
                          .replace("\\t", "\t")
                          .replace("\\r", "\r")
                          .replace("\\b", "\b")
                          .replace("\\f", "\f");
        return new StringLiteral(value);
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
    public Node visitPostOp(PostOpContext ctx) {
        Node expr = visit(ctx.expr());
        String op = ctx.INC().getText();
        return new PostOp(expr, op);
    }

    @Override
    public Node visitUnaryOp(UnaryOpContext ctx) {
        String op = ctx.PLUS() != null ? "+" : "-";
        Node expr = visit(ctx.expr());
        return new UnaryOp(op, expr);
    }

    @Override
    public Node visitParen(ParenContext ctx) {
        Node expr = visit(ctx.expr());
        return new Paren(expr);
    }

    @Override
    public Node visitCall(CallContext ctx) {
        String id = ctx.ID().getText();
        List<Node> args = new ArrayList<>();
        if (ctx.callArgs() != null) {
            args = ctx.callArgs().expr().stream()
                .map(this::visit)
                .collect(Collectors.toList());
        }
        return new Call(new Id(id), args);
    }

    @Override
    public Node visitLambda(LambdaContext ctx) {
        List<Id> args = new ArrayList<>();
    
        // Manejar diferentes formas de parámetros lambda
        if (ctx.lambdaParams() != null) {
            // Caso con paréntesis: (param) o (param1, param2)
            if (ctx.lambdaParams().param() != null) {
                args = ctx.lambdaParams().param().stream()
                    .map(param -> new Id(param.ID().getText()))
                    .collect(Collectors.toList());
            }
            // Caso con ID directo (sin paréntesis)
            else if (ctx.lambdaParams().ID() != null) {
                args.add(new Id(ctx.lambdaParams().ID().getText()));
            }
            // Caso con múltiples IDs en paréntesis
            else if (!ctx.lambdaParams().ID().isEmpty()) {
                args = ctx.lambdaParams().ID().stream()
                    .map(idNode -> new Id(idNode.getText()))
                    .collect(Collectors.toList());
            }
        }
        
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
    public Node visitLetDeclWithType(ExpressoParser.LetDeclWithTypeContext ctx) {
        String id = ctx.ID().getText();
        Node value = visit(ctx.expr());
        // Obtener el tipo si está especificado
        String type = ctx.TYPE() != null ? ctx.TYPE().getText() : "any";
        
        // Usar el constructor con tipo si existe, sino usar el constructor básico
        try {
            // Intentar usar el constructor con tipo
            return new Let(new Id(id), value, type);
        } catch (Exception e) {
            // Fallback al constructor básico si no existe el constructor con tipo
            return new Let(new Id(id), value);
        }
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
    public Node visitTernaryCondition(TernaryConditionContext ctx) {
        return new TernaryCondition(
            visit(ctx.expr(0)), 
            visit(ctx.expr(1)), 
            visit(ctx.expr(2))
        );
    }

    @Override
    public Node visitFunDecl(ExpressoParser.FunDeclContext ctx) {
        String name = ctx.ID().getText();
        Node body = visit(ctx.expr());
        
        // Procesar parámetros
        List<Id> params = new ArrayList<>();
        if (ctx.paramList() != null) {
            params = ctx.paramList().param().stream()
                .map(param -> new Id(param.ID().getText()))
                .collect(Collectors.toList());
        }
        
        // Si FunctionDecl no existe, crear un Let con una Lambda
        try {
            return new FunctionDecl(name, params, body);
        } catch (Exception e) {
            // Fallback: crear una variable con una lambda
            Lambda lambda = new Lambda(params, body);
            return new Let(new Id(name), lambda);
        }
    }
}