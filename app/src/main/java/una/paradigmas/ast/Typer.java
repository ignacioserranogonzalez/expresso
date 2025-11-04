package una.paradigmas.ast;

import una.paradigmas.node.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import una.paradigmas.ast.SymbolTable.SymbolType;

public class Typer implements Visitor<String> {
    private final SymbolTable symbolTable;

    public Typer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String toString() {
        return symbolTable.toString();
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
            case "float" -> true;
            case "call" -> true;
            case "string" -> true;
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
    public String visitId(Id id) {
        String type = symbolTable.getType(id.value());
        if (type == null) {
            throw new TypeException("Undefined variable: " + id.value());
        }
        return type;
    }

    @Override
    public String visitLet(Let let) {
        String id = let.id().value();
        
        if (symbolTable.getAllSymbols().contains(id)) throw new TypeException("Variable '" + id + "' already declared");
        
        String valueType = let.value().accept(this);
        String declaredType = let.type() != null ? let.type().accept(this) : valueType;
        
        if (!isCompatible(declaredType, valueType)) { // verificar inconsistencia entre el valor inferido y declarado con :
            throw new TypeException("Incompatible types in let: " + declaredType + " and " + valueType);
        }
        
        SymbolType symbolType = determineSymbolType(let.value(), let.type());
        symbolTable.addSymbol(id, symbolType, declaredType);
        return "";
    }

    @Override
    public String visitFun(Fun fun) {

        symbolTable.addSymbol(fun.name().value(), SymbolType.METHOD, fun.returnType().accept(this));

        fun.params().forEach(param -> 
            symbolTable.addSymbol(param.id().value(), SymbolType.PARAMETER, param.type().accept(this))
        );

        String bodyType = fun.body().accept(this);
        String returnType = fun.returnType().accept(this);

        if (!isCompatible(returnType, bodyType)) {
            throw new TypeException("Function body type " + bodyType + " doesn't match return type " + returnType);
        }

        return returnType;
    }

    @Override
    public String visitAddSub(AddSub addSub) {
        String leftType = addSub.left().accept(this);
        String rightType = addSub.right().accept(this);

        if (!isValidOperand(leftType)) 
            throw new TypeException("Left operand of " + addSub.op() + " must be numeric or string, got: " + leftType);
        
        if (!isValidOperand(rightType)) 
            throw new TypeException("Right operand of " + addSub.op() + " must be numeric or string, got: " + rightType);
        
        if (leftType.equals("string") || rightType.equals("string")) 
            return "string";
        
        return leftType.equals("float") || rightType.equals("float") ? "float" : "int";
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
            throw new TypeException("Incompatible types in ternary: " + thenType + " and " + elseType);
        }

        return thenType;
    }
    

    @Override
public String visitCall(Call call) {
    String id = call.id().value();
    
    if (!symbolTable.isMethod(id) && !symbolTable.isLambda(id) && !symbolTable.isConstructor(id)) {
        throw new TypeException("Undefined function or constructor: " + id);
    }

    call.args().stream().forEach(a -> a.accept(this));

    if (symbolTable.isConstructor(id)) {
        return symbolTable.getType(id); 
    }
    
    return "call";
}

    private boolean isCompatible(String expected, String actual) {
        if (expected.equals("any") || actual.equals("any")) return true;
        if (expected.equals(actual)) return true;

        if (expected.equals("float") && actual.equals("int")) return true;

        if (expected.contains("Object") || actual.contains("Object")) {
            return true;
        }

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
                                + typeName.substring(1);
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
    public String visitTupleType(TupleType tupleType) { // revisar Tuple normal, no el tuple de lambda
        throw new UnsupportedOperationException("Unimplemented method 'visitTupleType'");
    }

    @Override 
    public String visitLambda(Lambda l) {
        
        int argCount = l.args().size();
        String typeParams = String.join(", ", Collections.nCopies(argCount + 1, "Object"));
        
        return switch (argCount) {
            case 0 -> "Supplier<Object>";
            case 1 -> "Function<Object, Object>";
            case 2 -> "BiFunction<Object, Object, Object>";
            default -> "Function" + argCount + "<" + typeParams + ">";
        };
    }

    @Override
    public String visitArrowType(ArrowType arrowType) {
        
        return switch (arrowType.from()) {
            case TypeNode _ -> {
                String from = toWrapperType(arrowType.from().accept(this));
                String to = toWrapperType(arrowType.to().accept(this));
                
                if (from.equals("void")) {
                    String type = "Supplier<" + to + ">";
                    yield type;
                }
                else yield "Function<" + from + ", " + to + ">";
            }
            
            case TupleType tuple -> {
                List<String> paramTypes = tuple.types().stream()
                    .map(t -> t.accept(this))
                    .map(this::toWrapperType)
                    .collect(Collectors.toList());
                String returnType = toWrapperType(arrowType.to().accept(this));
                
                yield switch (paramTypes.size()) {
                    case 0 -> "Supplier<" + returnType + ">";
                    case 1 -> "Function<" + paramTypes.get(0) + ", " + returnType + ">";
                    case 2 -> "BiFunction<" + paramTypes.get(0) + ", " + paramTypes.get(1) + ", " + returnType + ">";
                    default -> {
                        String customName = "Function" + paramTypes.size();
                        yield customName + "<" + String.join(", ", paramTypes) + ", " + returnType + ">";
                    }
                };
            }
            
            default -> "Function<Object, Object>";
        };
        
    }

    @Override
    public String visitDataDecl(DataDecl dataDecl) {
        symbolTable.addSymbol(dataDecl.id(), SymbolType.DATA_TYPE, capitalizeFirst(dataDecl.id()));

        if(dataDecl.constructors() != null) 
            dataDecl.constructors().stream()
                .forEach(c -> 
                    symbolTable.addSymbol(
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
        
        if (!symbolTable.isConstructor(id)) {
            throw new TypeException("Undefined constructor: " + id);
        }
        
        String type = symbolTable.getType(id);
        return type != null ? type : "Object";
    }

    @Override
    public String visitMatch(Match match) {
        String matchedType = match.expr().accept(this);
        
        List<String> ruleTypes = match.rules().stream()
            .map(rule -> ((MatchRule) rule).accept(this))
            .collect(Collectors.toList());
        
        return ruleTypes.stream()
            .reduce((t1, t2) -> {
                if (isCompatible(t1, t2)) return t1;
                throw new TypeException("Incompatible types: " + t1 + " and " + t2);
            })
            .orElse(matchedType);
    }

    @Override
    public String visitMatchRule(MatchRule matchRule) {
        return matchRule.body().accept(this);
    }

    @Override
    public String visitDataPattern(DataPattern dataPattern) {
        String type = symbolTable.getType(dataPattern.name());
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
}