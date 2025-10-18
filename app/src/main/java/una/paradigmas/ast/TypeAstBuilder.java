package una.paradigmas.ast;

import una.paradigmas.ast.ExpressoParser.*;

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

public class TypeAstBuilder extends ExpressoBaseVisitor<Node> {
    
    @Override
    public Node visitTypeFlat(TypeFlatContext ctx) {
        return visit(ctx.flatType());
    }

    @Override
    public Node visitTypeTuple(TypeTupleContext ctx) {
        return visit(ctx.tuple());
    }

    @Override
    public Node visitTypeParen(TypeParenContext ctx) {
        return visit(ctx.type()); // elimina parentesis
    }

    @Override
    public Node visitFlatAtomic(FlatAtomicContext ctx) {
        return visit(ctx.atomic());
    }

    @Override
    public Node visitFlatArrow(FlatArrowContext ctx) {
        return visit(ctx.arrow());
    }

    @Override
    public Node visitAtomicAny(AtomicAnyContext ctx) {
        return new TypeNode("any");
    }

    @Override
    public Node visitAtomicVoid(AtomicVoidContext ctx) {
        return new TypeNode("void");
    }

    @Override
    public Node visitAtomicInt(AtomicIntContext ctx) {
        return new TypeNode("int");
    }

    @Override
    public Node visitAtomicFloat(AtomicFloatContext ctx) {
        return new TypeNode("float");
    }

    @Override
    public Node visitAtomicString(AtomicStringContext ctx) {
        return new TypeNode("string");
    }

    @Override
    public Node visitAtomicId(AtomicIdContext ctx) {
        return new TypeNode(ctx.ID().getText());
    }

    @Override
    public Node visitTupleType(TupleTypeContext ctx) {
        List<TypeNode> types = ctx.flatType().stream()
            .map(this::visit)
            .map(t -> (TypeNode) t)
            .collect(Collectors.toList());
        return new TupleType(types);
    }

    @Override
    public Node visitArrowTuple(ArrowTupleContext ctx) {
        Node from = visit(ctx.tuple());
        Node to = visit(ctx.flatType());
        return new ArrowType(from, to);
    }

    @Override
    public Node visitArrowAtomic(ArrowAtomicContext ctx) {
        Node from = visit(ctx.atomic());
        Node to = visit(ctx.flatType());
        return new ArrowType(from, to);
    }

    @Override
    public Node visitArrowParen(ArrowParenContext ctx) {
        Node from = visit(ctx.arrow());
        Node to = visit(ctx.flatType());
        return new ArrowType(from, to);
    }
}