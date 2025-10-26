package una.paradigmas.ast;

import una.paradigmas.ast.ExpressoParser.*;
import una.paradigmas.node.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    private final TypeAstBuilder typeAstBuilder = new TypeAstBuilder();
    private final Set<String> dataSymbols = new HashSet<>();

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
    public Node visitBoolean(BooleanContext ctx) {
        boolean value = Boolean.parseBoolean(ctx.BOOLEAN().getText());
        return new BooleanLiteral(value);
    }

    @Override
    public Node visitString(StringContext ctx) {
        String text = ctx.STRING().getText();
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
        String op = ctx.getText().substring(ctx.getText().length() - 2);
        return new PostOp(expr, op);
    }

    @Override
    public Node visitUnaryOp(UnaryOpContext ctx) {
        String op = ctx.PLUS() != null ? "+" : "-";
        Node expr = visit(ctx.expr());

        if (expr instanceof UnaryOp unaryExpr) {
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
        if (ctx.argList() != null) {
            args = ctx.argList().expr().stream()
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
        Node type = ctx.type() != null ? typeAstBuilder.visit(ctx.type()) : null;
        return new Let(new Id(id), value, type);
    }

    @Override
    public Node visitPrint(PrintContext ctx) {
        Node expr = visit(ctx.expr());
        return new Print(expr);
    }

    @Override
    public Node visitFunDecl(FunDeclContext ctx) {
        Id name = new Id(ctx.ID().getText());
        
        // parametros
        List<Fun.Param> params = List.of();
        if (ctx.paramList() != null) {
            params = ctx.paramList().param().stream()
                .map(paramCtx -> {
                    Id paramId = new Id(paramCtx.ID().getText());
                    Node paramType = paramCtx.type() != null ? 
                        typeAstBuilder.visit(paramCtx.type()) : new TypeNode("any");
                    return new Fun.Param(paramId, paramType);
                })
                .collect(Collectors.toList());
        }
    
        Node returnType = typeAstBuilder.visit(ctx.type());
        Node body = visit(ctx.expr());
        
        return new Fun(name, params, returnType, body);
    }

    @Override
    public Node visitDataDecl(DataDeclContext ctx) {
        String id = ctx.ID().getText();
        
        List<DataDecl.Constructor> constructors = ctx.constructorList() != null
            ? ctx.constructorList().constructor().stream()
                .map(constructorCtx -> {
                    String constructorId = constructorCtx.ID().getText();
                    dataSymbols.add(constructorId);
                    
                    List<DataDecl.Argument> arguments = constructorCtx.arguments() != null
                        ? constructorCtx.arguments().argument().stream()
                            .map(argCtx -> {
                                String argName = argCtx.ID() != null 
                                    ? argCtx.ID().getText() 
                                    : "";
                                Node argType = argCtx.type() != null ? 
                                    typeAstBuilder.visit(argCtx.type()) : new TypeNode("any");
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

    @Override
    public Node visitParam(ParamContext ctx) {
        return new Id(ctx.ID().getText());
    }

    @Override
    public Node visitTernaryCondition(TernaryConditionContext ctx) {
        return new TernaryCondition(visit(ctx.expr(0)), visit(ctx.expr(1)), visit(ctx.expr(2)));
    }

    @Override public Node visitConstructorInvocation(ConstructorInvocationContext ctx) { 
        String id = ctx.constructorExpr().ID().getText(); List<Node> args = new ArrayList<>(); 
        if (ctx.constructorExpr().argList() != null) { 
            args = ctx.constructorExpr().argList().expr().stream() 
                .map(this::visit) .collect(Collectors.toList()); 
            } 
        return new ConstructorInvocation(id, args); 
    }
    
    @Override
    public Node visitMatch(MatchContext ctx) {
        Node expr = visit(ctx.expr());
        List<Node> rules = ctx.matchRule().stream()
            .map(this::visitMatchRule)
            .collect(Collectors.toList());
        return new Match(expr, rules);
    }

    @Override
    public Node visitMatchRule(MatchRuleContext ctx) {
        Node pattern = visit(ctx.pattern());
        List<ExprContext> exprs = ctx.expr();
        
        // el ultimo expr es siempre el body
        Node body = visit(exprs.get(exprs.size() - 1));
        
        // si hay guard es el primer expr
        Node guard = ctx.expr().size() > 1 ? visit(exprs.get(0)) : null;
        
        return new MatchRule(pattern, guard, body);
    }

    @Override
    public Node visitDataOrVariablePattern(DataOrVariablePatternContext ctx) {
        String name = ctx.ID().getText();
        
        List<Node> subPatterns = Optional.ofNullable(ctx.pattern())
            .orElse(List.of())
            .stream()
            .map(this::visit)
            .map(p -> (Pattern) p)
            .collect(Collectors.toList());
        
        return dataSymbols.contains(name) 
            ? new DataPattern(name, subPatterns) 
            : new VariablePattern(name);
    }

    @Override
    public Node visitIntPattern(IntPatternContext ctx) {
        int value = Integer.parseInt(ctx.INT().getText());
        return new IntPattern(value);
    }

    @Override
    public Node visitStringPattern(StringPatternContext ctx) {
        String text = ctx.STRING().getText();
        String value = text.substring(1, text.length() - 1)
                        .replace("\\\\", "\\")
                        .replace("\\\"", "\"");
        return new StringPattern(value);
    }

    @Override
    public Node visitBooleanPattern(BooleanPatternContext ctx) {
        boolean value = Boolean.parseBoolean(ctx.BOOLEAN().getText());
        return new BooleanPattern(value);
    }

    @Override
    public Node visitNonePattern(NonePatternContext ctx) {
        return new NonePattern();
    }

    @Override
    public Node visitWildcardPattern(WildcardPatternContext ctx) {
        return new WildcardPattern();
    }

    @Override
    public Node visitBlank(BlankContext ctx) {
        return null;
    }
}