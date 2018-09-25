package org.drools.droolog.processor.v1;

import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import org.drools.droolog.meta.lib.v1.Term;

public class ObjectTermProcessor extends AbstractClassProcessor {

    public MethodDeclaration factoryMethodDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        String name = annotatedClassName + "ObjectTerm";
        ClassOrInterfaceType objectTermT = new ClassOrInterfaceType(
                null, name);
        return new MethodDeclaration(
                publicM,
                objectTermT,
                "create" + annotatedClassName
        ).setBody(new BlockStmt(new NodeList<>(new ReturnStmt(
                new ObjectCreationExpr(null, objectTermT, new NodeList<>())))));
    }

    public MethodDeclaration termGetter(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        String objectName = annotatedClassName + "Object";
        String objectTermName = annotatedClassName + "ObjectTerm";
        ClassOrInterfaceType objectT = new ClassOrInterfaceType(
                null, objectName);
        ClassOrInterfaceType objectTermT = new ClassOrInterfaceType(
                null, objectTermName);
        MethodCallExpr termOfBody = new MethodCallExpr(
                new EnclosedExpr(new CastExpr(objectTermT, new NameExpr("term"))),
                "$getStructure",
                new NodeList<>());
        return new MethodDeclaration(
                publicM,
                "termOf",
                new ClassOrInterfaceType(null, "Structure"),
                new NodeList<>(new Parameter(objectT, "term"))).setBody(
                new BlockStmt(new NodeList<>(new ReturnStmt(termOfBody))));
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString() + "Object";
        return super.classDeclaration(el)
                .setName(annotatedClassName + "Term")
                .setExtendedTypes(
                        new NodeList<>(JavaParser.parseClassOrInterfaceType(annotatedClassName)))
                .setImplementedTypes(
                        new NodeList<>(JavaParser.parseClassOrInterfaceType(Term.ObjectTerm.class.getCanonicalName())))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        String meta = el.getSimpleName().toString() + "Meta";
        ClassOrInterfaceType MetaT = new ClassOrInterfaceType(null, meta);
        ClassOrInterfaceType StructureT = new ClassOrInterfaceType(MetaT, "Structure");
        FieldAccessExpr singletonField = new FieldAccessExpr(new NameExpr(meta), "Instance");

        FieldDeclaration field =
                new FieldDeclaration(
                        EnumSet.of(Modifier.PRIVATE),
                        new VariableDeclarator(StructureT, "$struct",
                                               new MethodCallExpr(singletonField, "createStructure")));

        InitializerDeclaration init = new InitializerDeclaration(
                false,
                new BlockStmt(new NodeList<>(new ExpressionStmt(
                        new MethodCallExpr(
                                new NameExpr("$struct"),
                                "bind",
                                new NodeList<>(new ThisExpr()))))));

        MethodDeclaration getValue = getter(meta);
        MethodDeclaration setValue = setter(meta);

        return new NodeList<>(field, init, getValue, setValue);
    }

    private MethodDeclaration setter(String meta) {
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                "$setStructure",
                new VoidType(),
                new NodeList<>(new Parameter(
                        JavaParser.parseType(Term.Structure.class.getCanonicalName()),
                        "structure")))
                .setBody(new BlockStmt(new NodeList<>(new ExpressionStmt(
                        new AssignExpr(
                                new NameExpr("$struct"),
                                new CastExpr(new ClassOrInterfaceType(new ClassOrInterfaceType(null, meta), "Structure"),
                                             new NameExpr("structure")),
                                AssignExpr.Operator.ASSIGN))
                )));
    }

    private MethodDeclaration getter(String meta) {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new NameExpr("$struct"))));

        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new ClassOrInterfaceType(new ClassOrInterfaceType(null, meta), "Structure"),
                "$getStructure")
                .setBody(body);
    }
}
