package una.paradigmas.ast;

import una.paradigmas.node.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import una.paradigmas.ast.SymbolTable.SymbolType;

public class Typer implements Visitor<String> {
    private final Stack<SymbolTable> contextStack = new Stack<>();

    private final List<SymbolTable> contextList = new ArrayList<>(); // solo para debug ?

    public Typer(SymbolTable context) {
        this.contextStack.push(context);
        contextList.add(context);
    }

    private SymbolTable currentContext() {
        return contextStack.peek();
    }

    @Override
    public String toString() {
        return contextList.toString();
    }

    public boolean typeCheck(Program program) {
        try {
            System.out.println();
            visitProgram(program);
            System.out.println("Type checking passed !");
            return true;
        } catch (TypeException e) {
            System.err.println("[Type error]: " + e.getMessage());
        }
        return false;
    }

    private String toWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case "int" -> "Integer";
            case "float" -> "Float"; 
            case "boolean" -> "Boolean";
            default -> primitiveType; // String, Object, etc
        };
    }

    private SymbolType determineSymbolType(Node value, Node type) {

        if(type == null){
            return switch(value){
                case Fun _ -> SymbolType.METHOD;
                case Lambda _ -> SymbolType.LAMBDA;
                default -> SymbolType.VARIABLE;
            };
        }

        return switch (type) {
            case Lambda _ -> SymbolType.LAMBDA;
            case ArrowType _ -> SymbolType.LAMBDA;
            default -> SymbolType.VARIABLE;
        };
    }

    public boolean isValidOperand(String type){
        return switch(type){
            case "int" -> true;
            case "Integer" -> true;
            case "float" -> true;
            case "Float" -> true;
            case "call" -> true;
            default -> false;
        };
    }

    private String capitalizeFirst(String s) {
        return s == null || s.isEmpty() ? s 
            : s.substring(0, 1).toUpperCase() + s.substring(1);
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
    public String visitProgram(Program program) {
        program.statements().forEach(stat -> stat.accept(this));
        return "";
    }

    @Override
    public String visitId(Id id) { // busca en currentContext y si no en su getParent() hasta que sea null
        return java.util.stream.Stream
            .iterate(currentContext(), Objects::nonNull, SymbolTable::getParent)
            .map(ctx -> ctx.getType(id.value()))
            .filter(type -> !"unknown".equals(type))
            .findFirst()
            .orElseThrow(() -> new TypeException("Cannot find symbol: " + id.value()));
    }

    @Override
    public String visitLet(Let let) {
        String id = let.id().value();
        
        if (currentContext().getAllSymbols().contains(id)) throw new TypeException("Variable '" + id + "' already declared");
        
        String valueType = let.value().accept(this);
        String declaredType = let.type() != null ? let.type().accept(this) : valueType;

        // System.out.println(valueType);
        // System.out.println(declaredType);

        if (!isCompatible(declaredType, valueType))  // verificar inconsistencia entre el valor inferido y declarado con :
            throw new TypeException("Incompatible types in let: " + declaredType + " vs " + valueType);
        
        SymbolType symbolType = determineSymbolType(let.value(), let.type());
        currentContext().addSymbol(id, symbolType, declaredType);

        return "";
    }

    @Override
    public String visitFun(Fun fun) {

        SymbolTable funContext = new SymbolTable();
        funContext.setParent(currentContext());
        contextStack.push(funContext);
        contextList.add(funContext);

        try{
            currentContext().getParent().addSymbol(fun.name().value(), SymbolType.METHOD, fun.returnType().accept(this));

            fun.params().forEach(param -> 
                currentContext().addSymbol(param.id().value(), SymbolType.PARAMETER, param.type().accept(this))
            );
    
            String bodyType = fun.body().accept(this);
            String returnType = fun.returnType().accept(this);
    
            if (!isCompatible(returnType, bodyType)) {
                throw new TypeException("Function body type " + bodyType + " doesn't match return type " + returnType);
            }

            return returnType;

        } finally { contextStack.pop(); }        
    }

    @Override
    public String visitAddSub(AddSub addSub) {
        String leftType = addSub.left().accept(this);
        String rightType = addSub.right().accept(this);

        if (!isValidOperand(leftType)) {
            throw new TypeException("Left operand of " + addSub.op() + " must be numeric, got: " + leftType);
        }
        if (!isValidOperand(rightType)) {
            throw new TypeException("Right operand of " + addSub.op() + " must be numeric, got: " + rightType);
        }

        return "";
    }

    @Override
    public String visitMultDiv(MultDiv multDiv) {
        String leftType = multDiv.left().accept(this);
        String rightType = multDiv.right().accept(this);

        if (!isValidOperand(leftType)) {
            throw new TypeException("Left operand of " + multDiv.op() + " must be numeric, got: " + leftType);
        }
        if (!isValidOperand(rightType)) {
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

        if(call.callee() instanceof Id id){
            if (!currentContext().isMethod(id.value()) && !currentContext().isLambda(id.value())) {
                throw new TypeException("Undefined lambda: " + id.value());
            }
    
            call.args().stream().forEach(a -> a.accept(this));
        }

        return "call";
    }

    private boolean isCompatible(String expected, String actual) {
        if (expected.equals(actual)) 
            return true;

        if (expected.equals("any") || actual.equals("any")) 
            return true;

        if (expected.equals("float") && actual.equals("int")) 
            return true;

        if (expected.contains("Object") || actual.contains("Object"))
            return true;

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

    @Override
    public String visitType(TypeNode type) {
        return switch (type) {
            case TypeNode(var typeName) -> switch (typeName) {
                case "int" -> "int";
                case "float" -> "float";
                case "boolean" -> "boolean";
                case "string" -> "String";
                case "any" -> "Object";
                case "void" -> "void";
                default -> typeName.substring(0, 1)
                                .toUpperCase() 
                                + typeName.substring(1); // constructor
            };
            default -> "Object";
        };
    }

    @Override
    public String visitUnaryOp(UnaryOp unOp) {
        return unOp.expr().accept(this);
    }

    @Override
    public String visitPostOp(PostOp postOp) {
        return postOp.expr().accept(this);
    }

    @Override
    public String visitParen(Paren paren) {
        return "";
    }

    @Override 
    public String visitLambda(Lambda lambda) {
        SymbolTable lambdaContext = new SymbolTable();
        lambdaContext.setParent(currentContext());
        contextStack.push(lambdaContext);
        contextList.add(lambdaContext);
        
        try {

            lambda.params().forEach(param -> {
                String paramName = param.id().value();
                String paramType = param.type().accept(this);
                
                // verificar shadowing
                boolean existsInParent = Stream.iterate(
                    currentContext().getParent(),
                    ctx -> ctx != null, 
                    SymbolTable::getParent
                )
                .anyMatch(ctx -> !ctx.getType(paramName).equals("unknown"));
                if (existsInParent) throw new TypeException("Variable '" + paramName + "' is already defined");
                
                currentContext().addSymbol(paramName, SymbolType.PARAMETER, toWrapperType(paramType));
            });

            String bodyType = lambda.body().accept(this);
            String returnType = lambda.returnType().accept(this);
            
            // verificar que el cuerpo coincide con el return type declarado
            // if (!isCompatible(returnType, bodyType)) {
            //     throw new TypeException("Lambda return type mismatch: expected " + 
            //                         returnType + ", got " + bodyType);
            // }

            if (lambda.body() instanceof Lambda) {
                // x -> (y -> 1) debería ser Function<X, Function<Y, Z>>
                String paramType = toWrapperType(lambda.params().get(0).type().accept(this));
                return "Function<" + paramType + ", " + bodyType + ">";
            }

            int argCount = lambda.params().size();

            return switch (argCount) {
                case 0 -> "Supplier<" + toWrapperType(returnType) + ">";
                case 1 -> {
                    String paramType = toWrapperType(lambda.params().get(0).type().accept(this));
                    yield "Function<" + paramType + ", " + toWrapperType(returnType) + ">";
                }
                case 2 -> {
                    String param1Type = toWrapperType(lambda.params().get(0).type().accept(this));
                    String param2Type = toWrapperType(lambda.params().get(1).type().accept(this));
                    yield "BiFunction<" + param1Type + ", " + param2Type + ", " + toWrapperType(returnType) + ">";
                }
                default -> {
                    String paramTypes = lambda.params().stream()
                        .map(p -> toWrapperType(p.type().accept(this)))
                        .collect(Collectors.joining(", "));
                    yield "Function" + argCount + "<" + paramTypes + ", " + toWrapperType(returnType) + ">";
                }
            };
            
        } finally { 
            contextStack.pop(); 
        }
    }

    @Override
    public String visitDataDecl(DataDecl dataDecl) {
        currentContext().addSymbol(dataDecl.id(), SymbolType.DATA_TYPE, capitalizeFirst(dataDecl.id()));

        if(dataDecl.constructors() != null) 
            dataDecl.constructors().stream()
                .forEach(c -> 
                    currentContext().addSymbol(
                            c.id(), 
                            SymbolType.CONSTRUCTOR, 
                            capitalizeFirst(dataDecl.id())
                    )
                );

        return "";
    }

    @Override
    public String visitConstructorInvocation(ConstructorInvocation invocation) {
        String id = invocation.id();
        
        if (!currentContext().isConstructor(id)) {
            throw new TypeException("Undefined constructor: " + id);
        }
        
        String type = currentContext().getType(id);
        return type != null ? type : "Object";
    }

    @Override
    public String visitMatch(Match match) {
        return match.expr().accept(this);
    }

    @Override
    public String visitMatchRule(MatchRule matchRule) {
        return matchRule.body().accept(this);
    }

    @Override
    public String visitDataPattern(DataPattern dataPattern) {
        String type = currentContext().getType(dataPattern.name());
        return type != null ? type : "Object";
    }

    @Override  
    public String visitVariablePattern(VariablePattern variablePattern) {
        return "any";
    }

    @Override
    public String visitWildcardPattern(WildcardPattern wildcardPattern) {
        return "any";
    }

    @Override
    public String visitIntPattern(IntPattern intPattern) {
        return "int";
    }

    @Override
    public String visitFloatPattern(FloatPattern floatPattern) {
        return "float";
    }

    @Override
    public String visitStringPattern(StringPattern stringPattern) {
        return "string";
    }

    @Override
    public String visitBooleanPattern(BooleanPattern booleanPattern) {
        return "boolean";
    }

    @Override
    public String visitNonePattern(NonePattern nonePattern) {
        return "none";
    }

    @Override
    public String visitNone(NoneLiteral noneLiteral) {
        return "none";
    }

    @Override
    public String visitLogicalOp(LogicalOp boolOp) {
        return "boolean";
    }

    @Override
    public String visitRelOp(RelOp relOp) {
        return "boolean";
    }

    @Override
    public String visitNotOp(NotOp notOp) {
        return "boolean";
    }

    @Override
    public String visitCast(Cast cast) {
        return cast.type().accept(this);
    }

    @Override
    public String visitTupleType(TupleType tupleType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTupleType'");
    }

    @Override
    public String visitArrowType(ArrowType arrowType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitArrowType'");
    }
}