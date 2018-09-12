package org.drools.droolog.processor;

import java.util.EnumSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
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
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;

public class FieldProcessor {

    private final MethodDeclaration getter;
    private final FieldDeclaration field;
    private final Parameter parameter;
    private final ExpressionStmt assignment;

    public FieldProcessor(ExecutableElement field) {
        String fieldName = field.getSimpleName().toString();
        TypeMirror typeMirror = field.getReturnType();
        Type fieldType = JavaParser.parseType(typeMirror.toString());

        this.getter = makeGetter(fieldName, fieldType);
        this.field = makeField(fieldName, fieldType);
        this.parameter = makeParameter(fieldName, fieldType);
        this.assignment = makeAssignment(fieldName);
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

    private FieldDeclaration makeField(String fieldName, Type fieldType) {
        return new FieldDeclaration(
                EnumSet.of(Modifier.FINAL, Modifier.PRIVATE),
                fieldType,
                fieldName);
    }

    private MethodDeclaration makeGetter(String fieldName, Type fieldType) {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new FieldAccessExpr(new ThisExpr(), fieldName))));
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                fieldType,
                fieldName)
                .setBody(body);
    }

    private Parameter makeParameter(String fieldName, Type fieldType) {
        return new Parameter(
                fieldType,
                fieldName);
    }

    private ExpressionStmt makeAssignment(String fieldName) {
        return new ExpressionStmt(new AssignExpr(
                new FieldAccessExpr(new ThisExpr(), fieldName),
                new NameExpr(fieldName),
                AssignExpr.Operator.ASSIGN));
    }


    private MethodDeclaration makeSetter(String fieldName, Type fieldType) {
        AssignExpr assignExpr = new AssignExpr(
                new FieldAccessExpr(new ThisExpr(), fieldName),
                new NameExpr(fieldName),
                AssignExpr.Operator.ASSIGN);
        BlockStmt body = new BlockStmt(new NodeList<>(
                new ExpressionStmt(assignExpr)));
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new VoidType(),
                Fields.setterNameOf(fieldName))
                .addParameter(fieldType, fieldName)
                .setBody(body);
    }
}
