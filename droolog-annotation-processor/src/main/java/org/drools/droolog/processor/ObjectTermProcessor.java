package org.drools.droolog.processor;

import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import org.drools.droolog.meta.lib.Term;

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
                "create" + name
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
                .setMembers(members());
    }

    private NodeList<BodyDeclaration<?>> members() {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new NullLiteralExpr())));

        MethodDeclaration getValue = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new ClassOrInterfaceType(new ClassOrInterfaceType(null, "PersonMeta"), "Structure"),
                "$getStructure")
                .setBody(body);
        MethodDeclaration setValue = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                "$setStructure",
                new VoidType(),
                new NodeList<>(new Parameter(
                        JavaParser.parseType(Term.Structure.class.getCanonicalName()),
                        "structure")));

        return new NodeList<>(getValue, setValue);
    }
}
