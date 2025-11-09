package una.paradigmas.ast;

import una.paradigmas.node.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import una.paradigmas.ast.SymbolTable.SymbolInfo;
import una.paradigmas.ast.SymbolTable.SymbolType;

public class Typer implements Visitor<String> {
    private final Stack<SymbolTable> contextStack = new Stack<>();
    private final Map<String, SymbolTable> contextMap = new HashMap<>();
    private final Stack<Call> callStack = new Stack<>();

    private static final Map<String, Set<String>> TYPE_COMPATIBILITY = Map.of(
        "int", Set.of("Integer", "double", "Double", "any", "Object"),
        "Integer", Set.of("int", "double", "Double", "any", "Object"),
        "double", Set.of("Double", "int", "Integer", "any", "Object"),
        "Double", Set.of("double", "int", "Integer", "any", "Object"),
        "string", Set.of("String", "any", "Object"),
        "String", Set.of("string", "any", "Object"),
        "any", Set.of("any", "Object"),
        "Object", Set.of("Object", "any")
    );

    public Typer(SymbolTable context) {
        this.contextStack.push(context);
        contextMap.put("Global", context);
    }

    private SymbolTable currentContext() {
        return contextStack.peek();
    }

    @Override
    public String toString() {
        return contextMap.toString();
    }

    public boolean typeCheck(Program program) {
        try {
            System.out.println();
            visitProgram(program);
            System.out.println("Type checking passed !\n");
            return true;
        } catch (TypeException e) {
            System.err.println("[Type error]: " + e.getMessage());
        }
        return false;
    }

    private String toWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case "int" -> "Integer";
            case "double" -> "Double"; 
            case "boolean" -> "Boolean";
            case "string" -> "String";
            case "any" -> "Object";
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
            case "int", "Integer", "double", "Double", "string", "String", "boolean", "Boolean","call" -> true;
            default -> false;
        };
    }

    public boolean isValidCondition(String type){
        return switch(type){
            case "int", "Integer", "double", "Double", "boolean", "Boolean" -> true;
            default -> false;
        };
    }

    public String additionType(String left, String right){
        return switch (left) {
            case "string", "String" -> "String";  // string + cualquier cosa = string
            case "double", "Double" -> switch (right) {
                case "string", "String" -> "String";  // double + string = string
                default -> "double";         // double + numérico = double
            };
            case "int", "Integer" -> switch (right) {
                case "string", "String" -> "String";  // int + string = string
                case "double", "Double" -> "double";    // int + double = double
                default -> "int";           // int + int = int
            };
            default -> "Object";  // fallback
        };
    }

    private boolean isCompatible(String expected, String actual) {
        if (expected == null || actual == null) return false;

        if (expected.equalsIgnoreCase(actual))
            return true;

        if ("Object".equals(expected) || "any".equals(expected))
            return true;

        Set<String> compatibles = TYPE_COMPATIBILITY.get(expected);
        return compatibles != null && compatibles.contains(actual);
    }

    private boolean isVoidLikeBody(Node body) {
        return switch (body) {
            case Print _ -> true;        // print retorna null
            case NoneLiteral _ -> true;  // none es como void
            default -> false;            // Otros casos retornan valor
        };
    }

    private String calculateFunctionalType(Lambda lambda, String returnType) {
        if (lambda.body() instanceof Lambda) { // lambda anidada
            String paramType = toWrapperType(lambda.params().get(0).type().accept(this));
            return "Function<" + paramType + ", " + returnType + ">";
        }

        int argCount = lambda.params().size();
        boolean shouldBeConsumer = isVoidLikeBody(lambda.body());

        List<SymbolInfo> ctxParams = currentContext().getParametersList();
        
        return switch (argCount) {
            case 0 -> "Supplier<" + returnType + ">";
            case 1 -> {
                String paramType = toWrapperType(ctxParams.get(0).type());
                if (shouldBeConsumer) {
                    yield "Consumer<" + paramType + ">";  
                } else if (returnType.equals("Boolean") || returnType.equals("boolean")) {
                    yield "Predicate<" + paramType + ">";
                } else if (returnType.equals(ctxParams.get(0).type())) {
                    yield "UnaryOperator<" + returnType + ">";
                } else {
                    yield "Function<" + paramType + ", " + returnType + ">";
                }
            }
            case 2 -> {
                String param1Type = toWrapperType(ctxParams.get(0).type());
                String param2Type = toWrapperType(ctxParams.get(1).type());
                
                if (returnType.equals("Boolean") || returnType.equals("boolean")) {
                    yield "BiPredicate<" + param1Type + ", " + param2Type + ">";
                } else if (param1Type.equals(param2Type) && param1Type.equals(returnType)) {
                    yield "BinaryOperator<" + returnType + ">";
                } else {
                    yield "BiFunction<" + param1Type + ", " + param2Type + ", " + returnType + ">";
                }
            }
            default -> {
                String paramTypes = ctxParams.stream()
                    .map(p -> p.type())
                    .collect(Collectors.joining(", "));
                yield "Function" + argCount + "<" + paramTypes + ", " + returnType + ">";
            }
        };
    }

    private String capitalizeFirst(String s) {
        return s == null || s.isEmpty() ? s 
            : s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    //--------------------------------------------

    @Override
    public String visitInt(IntLiteral intLiteral) {
        return "int";
    }

    @Override
    public String visitDouble(DoubleLiteral doubleLiteral) {
        return "double";
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
        return "Program";
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
        
        if (!isCompatible(declaredType, valueType))  // verificar inconsistencia entre el valor inferido y declarado con :
            throw new TypeException("Incompatible types in let: " + declaredType + " vs " + valueType);
        
        SymbolType symbolType = determineSymbolType(let.value(), let.type()); // VARIABLE, LAMBDA, METHOD etc
        
        if (!(let.value() instanceof Lambda))
            currentContext().addSymbol(id, symbolType, declaredType);

        return declaredType;
    }

    @Override
    public String visitFun(Fun fun) {

        SymbolTable funContext = new SymbolTable();
        funContext.setParent(currentContext());
        contextStack.push(funContext);
        contextMap.put(fun.name().value(), funContext);

        try{

            currentContext().getParent().addSymbol(fun.name().value(), SymbolType.METHOD, fun.returnType().accept(this));

            Map<String, String> paramTypes = new HashMap<>();
            fun.params().forEach(param -> {
                String paramName = param.id().value();
                String paramType = param.type().accept(this);
                paramTypes.put(paramName, paramType);
                currentContext().addSymbol(paramName, SymbolType.PARAMETER, paramType);
            });
    
            String bodyType = fun.body().accept(this);
            String declaredReturnType = fun.returnType().accept(this);
            String returnType = declaredReturnType.equals("Object") ? 
                                toWrapperType(bodyType) : 
                                declaredReturnType;

                currentContext().getParent().addSymbol(fun.name().value(), SymbolType.METHOD, returnType);
            
    
            if (!isCompatible(returnType, bodyType)) {
                throw new TypeException("Function body type " + bodyType + " doesn't match return type " + returnType);
            }

            return returnType;

        } finally { contextStack.pop(); }        
    }

    @Override 
    public String visitLambda(Lambda lambda) {
        SymbolTable lambdaContext = new SymbolTable();
        lambdaContext.setParent(currentContext());
        contextStack.push(lambdaContext);
        contextMap.put(lambda.name(), lambdaContext);

        Stack<Call> previousCallStack = new Stack<>();
        previousCallStack.addAll(callStack); 
        callStack.clear(); 
        
        try {
            lambda.params().forEach(param -> {
                String paramName = param.id().value();
                String paramType = param.type().accept(this);
                
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
            String declaredReturnType = lambda.returnType().accept(this);
            String returnType = declaredReturnType.equals("Object") ? 
                                toWrapperType(bodyType) : 
                                declaredReturnType;

            String functionalType = calculateFunctionalType(lambda, returnType);
            
            currentContext().getParent().addSymbol(
                lambda.name(), 
                SymbolType.LAMBDA, 
                returnType,
                functionalType 
            );

            return functionalType;
            
        } finally { 
            contextStack.pop(); 
            callStack.clear();
            callStack.addAll(previousCallStack);
        }
    }

    @Override
    public String visitCall(Call call) {
        return switch (call.callee()) {
            case Id id -> {

                SymbolTable context = Stream.iterate( // primero buscar si esta definido en un contexto
                        currentContext(), 
                        ctx -> ctx != null, 
                        SymbolTable::getParent
                    )
                    .filter(ctx -> ctx.isMethod(id.value()) || ctx.isLambda(id.value()))
                    .findFirst()
                    .orElseThrow(() -> new TypeException("Symbol " + id.value() + " is not defined")); 

                call.args().forEach(arg -> arg.accept(this));

                callStack.push(call); // callStack es local para cada lambda
                
                if (context.isMethod(id.value()) || context.isLambda(id.value())) {
                    yield context.getType(id.value()); // return type de method/lambda
                } else yield "Object";
            }
            default -> {
                call.args().forEach(arg -> arg.accept(this));
                yield "Object";
            }
        };
    }
    
    @Override
    public String visitAddSub(AddSub addSub) {
        String leftType = addSub.left().accept(this);
        String rightType = addSub.right().accept(this);

        if (!isValidOperand(leftType)) {
            throw new TypeException("Left operand of " + addSub.op() + " must be numeric or string, got: " + leftType);
        }
        if (!isValidOperand(rightType)) {
            throw new TypeException("Right operand of " + addSub.op() + " must be numeric or string, got: " + rightType);
        }

        // queda pendiente NO permitir si es resta (restar solo numericos).
        // reglas de tipo para suma
        String returnType = additionType(leftType, rightType);
        return returnType;
    }

    @Override
    public String visitMultDiv(MultDiv multDiv) {
        String leftType = multDiv.left().accept(this);
        String rightType = multDiv.right().accept(this);

        if (!isValidOperand(leftType)) 
            throw new TypeException("Left operand of " + multDiv.op() + " must be numeric, got: " + leftType);
        
        if (!isValidOperand(rightType)) 
            throw new TypeException("Right operand of " + multDiv.op() + " must be numeric, got: " + rightType);
        

        return leftType.equals("double") || rightType.equals("double") ? "double" : "int";
    }

    @Override
    public String visitTernaryCondition(TernaryCondition ternary) {
        String condType = ternary.condition().accept(this);

        if (!isValidCondition(condType)) 
            throw new TypeException("Condition must be boolean or numeric, got: " + condType);

        String thenType = ternary.value1().accept(this);
        String elseType = ternary.value2().accept(this);

        if (!isCompatible(thenType, elseType)) 
            throw new TypeException("Incompatible types in ternary: " + thenType + " vs " + elseType);

        return thenType;
    }

    @Override public String visitPrint(Print print) {
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
                case "double" -> "double";
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
        return paren.expr().accept(this);
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

        return dataDecl.id();
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
        return "double";
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

    @Override public String visitTupleType(TupleType tupleType) { return ""; }
    @Override public String visitArrowType(ArrowType arrowType) { return ""; }
}