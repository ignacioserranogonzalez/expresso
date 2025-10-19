package una.paradigmas.ast;

import una.paradigmas.ast.ExpressoParser.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        List<Node> statements = ctx.stat().stream().map(this::visit).filter(expr -> expr != null)
                                    .collect(Collectors.toList());
        return new Program(statements);
    }

    //----------------- type

    @Override
    public Node visitIntType(IntTypeContext ctx) {
        return new TypeNode("int");
    }

    @Override
    public Node visitFloatType(FloatTypeContext ctx) {
        return new TypeNode("float");
    }

    @Override
    public Node visitBooleanType(BooleanTypeContext ctx) {
        return new TypeNode("boolean");
    }

    @Override
    public Node visitStringType(StringTypeContext ctx) {
        return new TypeNode("string");
    }

    @Override
    public Node visitAnyType(AnyTypeContext ctx) {
        return new TypeNode("any");
    }
    @Override
    public Node visitCustomType(CustomTypeContext ctx) {
        return new TypeNode(ctx.ID().getText());
    }

//----------------- dataDecl
     @Override
    public Node visitDataDecl(DataDeclContext ctx) {
        String id = ctx.ID().getText();
        
        List<DataDecl.Constructor> constructors = ctx.constructorList() != null
            ? ctx.constructorList().constructor().stream()
                .map(constructorCtx -> {
                    String constructorId = constructorCtx.ID().getText();
                    
                    List<DataDecl.Argument> arguments = constructorCtx.arguments() != null
                        ? constructorCtx.arguments().argument().stream()
                            .map(argCtx -> {
                                String argName = argCtx.ID() != null 
                                    ? argCtx.ID().getText() 
                                    : "";
                                Node argType = visit(argCtx.type());
                                return new DataDecl.Argument(argName, argType);
                            })
                            .collect(Collectors.toList())
                        : List.of();
                    
                    return new DataDecl.Constructor(constructorId, arguments);
                })
                .collect(Collectors.toList())
            : List.of();
        
        return new DataDecl(id, constructors);
    }

    //----------------- expr

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
    public Node visitBoolean(BooleanContext ctx) {
        boolean value = Boolean.parseBoolean(ctx.BOOLEAN().getText());
        return new BooleanLiteral(value);
    }

    @Override
    public Node visitString(StringContext ctx) {
        String text = ctx.STRING().getText();
        // Remover comillas externas y procesar escapes
        String value = text.substring(1, text.length() - 1)
                        .replace("\\\\", "\\")
                        .replace("\\\"", "\"")
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
        
        // Si la expresión ya es un UnaryOp con el mismo operador, 
        // necesitamos preservar ambos operadores
        if (expr instanceof UnaryOp unaryExpr) {
            // Para --x: crear un nuevo UnaryOp que contenga el existente
            return new UnaryOp(op, unaryExpr);
        }
        
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
    
        if (ctx.lambdaParams().ID() != null) {
            args = ctx.lambdaParams().ID().stream()
                .map(idNode -> new Id(idNode.getText()))
                .collect(Collectors.toList());
        }
        
        Node expr = visit(ctx.expr());
        return new Lambda(args, expr);
    }

    @Override
        public Node visitLetDecl(LetDeclContext ctx) {
        String id = ctx.ID().getText();
        Node value = visit(ctx.expr());
        Node type = Optional.ofNullable(ctx.type())
                        .map(this::visit)
                        .orElse(new TypeNode("any"));
        return new Let(new Id(id), value, type);
    }

    @Override
    public Node visitPrint(PrintContext ctx) {
        Node expr = visit(ctx.expr());
        return new Print(expr);
    }

    @Override
    public Node visitTernaryCondition(TernaryConditionContext ctx) {
        return new TernaryCondition(visit(ctx.expr(0)), visit(ctx.expr(1)), visit(ctx.expr(2)));
    }

    @Override
    public Node visitBlank(BlankContext ctx) {
        return null;
    }
}