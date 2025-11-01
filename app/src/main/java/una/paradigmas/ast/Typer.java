package una.paradigmas.ast;

import una.paradigmas.node.*;
import java.util.HashMap;
import java.util.Map;

public class Typer implements Visitor<String> {
    private final Map<String, String> context = new HashMap<>();
    private final SymbolTable symbolTable;

    public Typer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public boolean typeCheck(Program program) {
        try {
            System.out.println();
            visitProgram(program);
            System.out.println("Type checking passed!");
            return true;
        } catch (TypeException e) {
            System.err.println("[Type error]: " + e.getMessage());
        }
        return false;
    }

    @Override
    public String visitProgram(Program program) {
        program.statements().forEach(stat -> stat.accept(this));
        return "void";
    }

    @Override
    public String visitLet(Let let) {
        String id = let.id().value();
        
        if (context.containsKey(id)) throw new TypeException("Variable '" + id + "' already declared");
        
        String valueType = let.value().accept(this);
        String declaredType = let.type() != null ? let.type().accept(this) : valueType;
        
        if (!isCompatible(declaredType, valueType)) {
            throw new TypeException("Incompatible types in let: " + declaredType + " vs " + valueType);
        }
        
        context.put(id, declaredType);
        return declaredType;
    }

    @Override
    public String visitFun(Fun fun) {
        fun.params().stream().reduce((a, b) -> { context.put(a.id().value(), a.type().accept(this)); return b; })
                .ifPresent(p -> context.put(p.id().value(), p.type().accept(this)));

        String bodyType = fun.body().accept(this);
        String returnType = fun.returnType().accept(this);

        if (!isCompatible(returnType, bodyType)) {
            throw new TypeException("Function body type " + bodyType + " doesn't match return type " + returnType);
        }

        return returnType;
    }

    @Override
    public String visitInt(IntLiteral intLiteral) {
        return "int";
    }

    @Override
    public String visitFloat(FloatLiteral floatLiteral) {
        return "float";
    }

    @Override
    public String visitBoolean(BooleanLiteral booleanLiteral) {
        return "boolean";
    }

    @Override
    public String visitString(StringLiteral stringLiteral) {
        return "string";
    }

    @Override
    public String visitId(Id id) {
        String type = context.get(id.value());
        if (type == null) {
            throw new TypeException("Undefined variable: " + id.value());
        }
        return type;
    }

    @Override
    public String visitAddSub(AddSub addSub) {
        String leftType = addSub.left().accept(this);
        String rightType = addSub.right().accept(this);

        if (!leftType.equals("int") && !leftType.equals("float")) {
            throw new TypeException("Left operand of " + addSub.op() + " must be numeric, got: " + leftType);
        }
        if (!rightType.equals("int") && !rightType.equals("float")) {
            throw new TypeException("Right operand of " + addSub.op() + " must be numeric, got: " + rightType);
        }

        return leftType.equals("float") || rightType.equals("float") ? "float" : "int";
    }

    @Override
    public String visitMultDiv(MultDiv multDiv) {
        String leftType = multDiv.left().accept(this);
        String rightType = multDiv.right().accept(this);

        if (!leftType.equals("int") && !leftType.equals("float")) {
            throw new TypeException("Left operand of " + multDiv.op() + " must be numeric, got: " + leftType);
        }
        if (!rightType.equals("int") && !rightType.equals("float")) {
            throw new TypeException("Right operand of " + multDiv.op() + " must be numeric, got: " + rightType);
        }

        return leftType.equals("float") || rightType.equals("float") ? "float" : "int";
    }

    @Override
    public String visitTernaryCondition(TernaryCondition ternary) {
        String condType = ternary.condition().accept(this);

        if (!condType.equals("boolean") && !condType.equals("int")) {
            throw new TypeException("Condition must be boolean or int, got: " + condType);
        }

        String thenType = ternary.value1().accept(this);
        String elseType = ternary.value2().accept(this);

        if (!isCompatible(thenType, elseType)) {
            throw new TypeException("Incompatible types in ternary: " + thenType + " vs " + elseType);
        }

        return thenType;
    }

    @Override
    public String visitCall(Call call) {
        if (!symbolTable.isFunction(call.id().value())) {
            throw new TypeException("Undefined function: " + call.id().value());
        }

        call.args().stream().reduce((a, b) -> { a.accept(this); return b; }).ifPresent(n -> n.accept(this));

        return "any";
    }

    private boolean isCompatible(String expected, String actual) {
        if (expected.equals("any") || actual.equals("any")) return true;
        if (expected.equals(actual)) return true;

        if (expected.equals("float") && actual.equals("int")) return true;

        return false;
    }

    @Override public String visitPrint(Print print) {
        print.expr().accept(this);
        return "void";
    }

    @Override
    public String visitPrintExpr(PrintExpr print) {
        print.expr().accept(this);
        return "void";
    }

    @Override public String visitPow(Pow pow) {
        pow.left().accept(this);
        pow.right().accept(this);
        return "void";
    }

    @Override public String visitLambda(Lambda lambda) {
        return "any";
    }

    @Override
    public String visitType(TypeNode type) {
        return type.typeName();
    }

    @Override
    public String visitUnaryOp(UnaryOp unOp) {
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryOp'");
    }

    @Override
    public String visitPostOp(PostOp postOp) {
        throw new UnsupportedOperationException("Unimplemented method 'visitPostOp'");
    }

    @Override
    public String visitParen(Paren paren) {
        return "void";
    }

    @Override
    public String visitTupleType(TupleType tupleType) {
        throw new UnsupportedOperationException("Unimplemented method 'visitTupleType'");
    }

    @Override
    public String visitArrowType(ArrowType arrowType) {
        return "any";
    }

    @Override
    public String visitDataDecl(DataDecl dataDecl) {
        throw new UnsupportedOperationException("Unimplemented method 'visitDataDecl'");
    }

    @Override
    public String visitConstructorInvocation(ConstructorInvocation constructorInvocation) {
        throw new UnsupportedOperationException("Unimplemented method 'visitConstructorInvocation'");
    }

    @Override
    public String visitMatch(Match match) {
        throw new UnsupportedOperationException("Unimplemented method 'visitMatch'");
    }

    @Override
    public String visitMatchRule(MatchRule matchRule) {
        throw new UnsupportedOperationException("Unimplemented method 'visitMatchRule'");
    }

    @Override
    public String visitDataPattern(DataPattern constructorPattern) {
        throw new UnsupportedOperationException("Unimplemented method 'visitDataPattern'");
    }

    @Override
    public String visitVariablePattern(VariablePattern variablePattern) {
        throw new UnsupportedOperationException("Unimplemented method 'visitVariablePattern'");
    }

    @Override
    public String visitWildcardPattern(WildcardPattern wildcardPattern) {
        throw new UnsupportedOperationException("Unimplemented method 'visitWildcardPattern'");
    }

    @Override
    public String visitIntPattern(IntPattern intPattern) {
        throw new UnsupportedOperationException("Unimplemented method 'visitIntPattern'");
    }

    @Override
    public String visitFloatPattern(FloatPattern floatPattern) {
        throw new UnsupportedOperationException("Unimplemented method 'visitFloatPattern'");
    }

    @Override
    public String visitStringPattern(StringPattern stringPattern) {
        throw new UnsupportedOperationException("Unimplemented method 'visitStringPattern'");
    }

    @Override
    public String visitBooleanPattern(BooleanPattern booleanPattern) {
        throw new UnsupportedOperationException("Unimplemented method 'visitBooleanPattern'");
    }

    @Override
    public String visitNonePattern(NonePattern nonePattern) {
        throw new UnsupportedOperationException("Unimplemented method 'visitNonePattern'");
    }

    @Override
    public String visitNone(NoneLiteral noneLiteral) {
        throw new UnsupportedOperationException("Unimplemented method 'visitNone'");
    }

    @Override
    public String visitBoolOp(LogicalOp boolOp) {
        throw new UnsupportedOperationException("Unimplemented method 'visitBoolOp'");
    }

    @Override
    public String visitRelOp(RelOp relOp) {
        throw new UnsupportedOperationException("Unimplemented method 'visitRelOp'");
    }

    @Override
    public String visitNotOp(NotOp notOp) {
        throw new UnsupportedOperationException("Unimplemented method 'visitNotOp'");
    }

    @Override
    public String visitCast(Cast cast) {
        throw new UnsupportedOperationException("Unimplemented method 'visitCast'");
    }
}