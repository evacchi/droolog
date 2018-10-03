package org.drools.droolog.processor.v4;

import java.util.EnumSet;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.drools.droolog.meta.lib.v4.ObjectTerm;

public class FieldProcessor {

    private final MethodDeclaration getter;
    private final FieldDeclaration field;
    private final Parameter parameter;
    private final ExpressionStmt assignment;
    private final StringLiteralExpr literal;
    private final String name;
    private final Type type;
    private final FieldAccessExpr access;
    private final boolean isStructure;

    public FieldProcessor(VariableElement field) {
        this.name = field.getSimpleName().toString();
        TypeMirror typeMirror = field.asType();
        this.type = JavaParser.parseType(typeMirror.toString());
        this.isStructure = field.getAnnotation(ObjectTerm.class) != null;
        this.getter = makeGetter();
        this.field = makeField();
        this.parameter = new Parameter(
                type,
                name);
        this.assignment = makeAssignment();
        this.literal = new StringLiteralExpr(name);
        this.access = new FieldAccessExpr(new ThisExpr(), name);
    }

    public String name() {
        return name;
    }

    public NameExpr nameExpr() {
        return new NameExpr(name);
    }


    public Type type() {
        return type;
    }

    public MethodDeclaration getter() {
        return getter;
    }

    public FieldDeclaration field() {
        return field;
    }

    public Parameter parameter() {
        return parameter;
    }

    public ExpressionStmt assignment() {
        return assignment;
    }

    public StringLiteralExpr literal() {
        return literal;
    }

    public FieldAccessExpr access() {
        return access;
    }

    private FieldDeclaration makeField() {
        return new FieldDeclaration(
                EnumSet.of(Modifier.FINAL, Modifier.PRIVATE),
                type,
                name);
    }

    private MethodDeclaration makeGetter() {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new FieldAccessExpr(new ThisExpr(), name))));
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                type,
                name)
                .setBody(body);
    }

    private ExpressionStmt makeAssignment() {
        return new ExpressionStmt(new AssignExpr(
                new FieldAccessExpr(new ThisExpr(), name),
                new NameExpr(name),
                AssignExpr.Operator.ASSIGN));
    }

    private MethodDeclaration makeSetter() {
        AssignExpr assignExpr = new AssignExpr(
                new FieldAccessExpr(new ThisExpr(), name),
                new NameExpr(name),
                AssignExpr.Operator.ASSIGN);
        BlockStmt body = new BlockStmt(new NodeList<>(
                new ExpressionStmt(assignExpr)));
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new VoidType(),
                name)
                .addParameter(type, name)
                .setBody(body);
    }

    public boolean isStructure() {
        return this.isStructure;
    }
}
