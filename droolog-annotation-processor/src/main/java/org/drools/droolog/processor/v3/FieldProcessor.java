package org.drools.droolog.processor.v3;

import java.util.EnumSet;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.drools.droolog.meta.lib4.TermState;

public class FieldProcessor {

    static final Type TermStateT = JavaParser.parseType(TermState.class.getCanonicalName());

    private final VariableElement element;
    private final String name;
    private final int index;
    private final Type type;

    public FieldProcessor(VariableElement field, int index) {
        this.name = field.getSimpleName().toString();
        this.element = field;
        this.index = index;
        TypeMirror typeMirror = field.asType();
        this.type = JavaParser.parseType(typeMirror.toString());
    }

    public String name() {
        return name;
    }

    public NameExpr nameExpr() {
        return new NameExpr(name);
    }

    public FieldDeclaration index() {
        return new FieldDeclaration(
                EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
                new VariableDeclarator(PrimitiveType.intType(), name, new IntegerLiteralExpr(index)));
    }

    public FieldDeclaration termState() {
        return new FieldDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new VariableDeclarator(TermStateT, String.format("%s_%s", name, ObjectProcessor.TermState), new FieldAccessExpr(new TypeExpr(TermStateT), "Free")));
    }

    public SwitchEntryStmt switchEntry(String suffix) {
        return new SwitchEntryStmt(new NameExpr(name), new NodeList<>(
                new ReturnStmt(new NameExpr(String.format("%s_%s", name, suffix)))
        ));
    }

    public Type type() {
        return type;
    }

    private FieldDeclaration field() {
        return new FieldDeclaration(
                EnumSet.of(Modifier.FINAL, Modifier.PRIVATE),
                type,
                name);
    }

    private MethodDeclaration getter() {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new FieldAccessExpr(new ThisExpr(), name))));
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                type,
                name)
                .setBody(body);
    }

    private ExpressionStmt assignment() {
        return new ExpressionStmt(new AssignExpr(
                new FieldAccessExpr(new ThisExpr(), name),
                new NameExpr(name),
                AssignExpr.Operator.ASSIGN));
    }

    private MethodDeclaration setter() {
        AssignExpr assignExpr = new AssignExpr(
                new FieldAccessExpr(new ThisExpr(), name),
                new NameExpr(name),
                AssignExpr.Operator.ASSIGN);
        BlockStmt body = new BlockStmt(new NodeList<>(
                new ExpressionStmt(assignExpr)));
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new VoidType(),
                Fields.setterNameOf(name))
                .addParameter(type, name)
                .setBody(body);
    }
}
