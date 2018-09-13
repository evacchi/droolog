package org.drools.droolog.processor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static java.util.stream.Collectors.toList;

public class ObjectProcessor {

    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        return new ClassOrInterfaceDeclaration()
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setName(annotatedClassName + "Object")
                .setImplementedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(annotatedClassName)))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        String generatedClassName = el.getSimpleName().toString() + "Object";

        NodeList<BodyDeclaration<?>> bodyDeclarations = new NodeList<>();
        List<FieldProcessor> fields = ElementFilter.methodsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());

        makeFields(bodyDeclarations, fields);
        bodyDeclarations.add(
                makeConstructor(fields, generatedClassName));
        makeGetters(bodyDeclarations, fields);
        bodyDeclarations.add(
                makeToString(fields, generatedClassName));
        bodyDeclarations.add(
                makeEquals(fields, generatedClassName));

        return bodyDeclarations;
    }

    private void makeGetters(NodeList<BodyDeclaration<?>> bodyDeclarations, List<FieldProcessor> fields) {
        for (FieldProcessor fp : fields) {
            bodyDeclarations.add(fp.getter());
        }
    }

    private ConstructorDeclaration makeConstructor(List<FieldProcessor> fields, String generatedClassName) {
        ConstructorDeclaration ctor = new ConstructorDeclaration(generatedClassName);
        NodeList<Parameter> parameters = ctor.getParameters();
        NodeList<Statement> statements = ctor.getBody().asBlockStmt().getStatements();
        for (FieldProcessor fp : fields) {
            parameters.add(fp.parameter());
            statements.add(fp.assignment());
        }
        return ctor;
    }

    private void makeFields(NodeList<BodyDeclaration<?>> bodyDeclarations, List<FieldProcessor> fields) {
        for (FieldProcessor fp : fields) {
            bodyDeclarations.add(fp.field());
        }
    }

    private MethodDeclaration makeEquals(List<FieldProcessor> fields, String generatedClassName) {
        ClassOrInterfaceType generatedClassNameExpr = new ClassOrInterfaceType(null, generatedClassName);
        MethodDeclaration m = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                "equals",
                new ClassOrInterfaceType(null, "boolean"),
                new NodeList<>(new Parameter(new ClassOrInterfaceType(null, "Object"), "other")));

        ArrayList<Expression> exprs = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            FieldProcessor field = fields.get(i);
            exprs.add(new MethodCallExpr(new NameExpr(Objects.class.getCanonicalName()), "equals", new NodeList<>(
                    field.access(),
                    new FieldAccessExpr(new NameExpr("o"), field.name()))));
        }

        BlockStmt blockStmt = new BlockStmt(new NodeList<>(
                new IfStmt(
                        new InstanceOfExpr(new NameExpr("other"), new ClassOrInterfaceType(null, generatedClassName)),
                        new BlockStmt(new NodeList<>(
                                new ExpressionStmt(
                                        new VariableDeclarationExpr(
                                                new VariableDeclarator(
                                                        generatedClassNameExpr,
                                                        "o",
                                                        new CastExpr(
                                                                new ClassOrInterfaceType(null, generatedClassName),
                                                                new NameExpr("other"))))),
                                new ReturnStmt(
                                        concat(exprs.get(0), exprs.subList(
                                                0, exprs.size() - 1), (l, r) -> new BinaryExpr(l, r, BinaryExpr.Operator.AND)))
                        )),
                        new ReturnStmt(new BooleanLiteralExpr(false)))));

        return m.setBody(blockStmt);
    }

    private MethodDeclaration makeToString(List<FieldProcessor> fields, String generatedClassName) {
        MethodDeclaration m = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new ClassOrInterfaceType(null, "String"),
                "toString");

        StringLiteralExpr begin = new StringLiteralExpr(generatedClassName + "(");
        StringLiteralExpr end = new StringLiteralExpr(")");

        ArrayList<Expression> exprs = new ArrayList<>();
        for (int i = 0; i < fields.size() - 1; i++) {
            FieldProcessor field = fields.get(i);
            exprs.add(field.literal());
            exprs.add(new StringLiteralExpr("="));
            exprs.add(field.access());
            exprs.add(new StringLiteralExpr(", "));
        }
        FieldProcessor field = fields.get(fields.size() - 1);
        exprs.add(field.literal());
        exprs.add(new StringLiteralExpr("="));
        exprs.add(field.access());

        BlockStmt blockStmt = new BlockStmt(new NodeList<>(new ReturnStmt(
                concat(begin, end, exprs, (l, r) -> new BinaryExpr(l, r, BinaryExpr.Operator.PLUS)))));

        return m.setBody(blockStmt);
    }

    private Expression concat(Expression begin, Expression end, List<Expression> exprs, BiFunction<Expression, Expression, Expression> combiner) {
        return combiner.apply(concat(begin, exprs.subList(0, exprs.size()), combiner), end);
    }

    private Expression concat(Expression e, List<Expression> exprs, BiFunction<Expression, Expression, Expression> combiner) {
        if (exprs.isEmpty()) {
            return e;
        } else {
            return concat(
                    combiner.apply(e, exprs.get(0)),
                    exprs.subList(1, exprs.size()), combiner);
        }
    }
}
