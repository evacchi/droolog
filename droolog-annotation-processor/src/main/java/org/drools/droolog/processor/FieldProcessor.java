package org.drools.droolog.processor;

import java.util.EnumSet;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
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
    private final MethodDeclaration setter;

    public FieldProcessor(Element field) {
        String fieldName = field.getSimpleName().toString();
        TypeMirror typeMirror = field.asType();
        Type fieldType = JavaParser.parseType(typeMirror.toString());

        this.getter = makeGetter(fieldName, fieldType);
        this.setter = makeSetter(fieldName, fieldType);
    }

    public MethodDeclaration getter() {
        return getter;
    }

    public MethodDeclaration setter() {
        return setter;
    }

    private MethodDeclaration makeGetter(String fieldName, Type fieldType) {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new FieldAccessExpr(new ThisExpr(), fieldName))));
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                fieldType,
                Fields.getterNameOf(fieldName))
                .setBody(body);
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
