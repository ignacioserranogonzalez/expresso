package una.paradigmas.ast;

import org.antlr.v4.parse.ANTLRParser.wildcard_return;

import una.paradigmas.node.*;

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
 */

public interface Visitor<T> {
    T visitProgram(Program program);
    T visitType(TypeNode num);
    T visitInt(IntLiteral num);
    T visitFloat(FloatLiteral num);
    T visitBoolean(BooleanLiteral num);
    T visitString(StringLiteral num);
    T visitId(Id id);
    T visitPow(Pow pow);
    T visitMultDiv(MultDiv multDiv);
    T visitAddSub(AddSub addSub);
    T visitUnaryOp(UnaryOp unOp);
    T visitPostOp(PostOp postOp);
    T visitParen(Paren paren);
    T visitLet(Let let);
    T visitPrint(Print print);
    T visitFun(Fun fun);
    T visitLambda(Lambda lambda);
    T visitCall(Call call);
    T visitTernaryCondition(TernaryCondition ternary);
    T visitTupleType(TupleType tupleType);
    T visitArrowType(ArrowType arrowType);
    T visitDataDecl(DataDecl dataDecl);
    T visitConstructorInvocation(ConstructorInvocation constructorInvocation);
    T visitMatch(Match match);
    T visitMatchRule(MatchRule matchRule);
    T visitConstructorPattern(ConstructorPattern constructorPattern);
    T visitVariablePattern(VariablePattern variablePattern);
    T visitWildcardPattern(WildcardPattern wildcardPattern);
    
}
