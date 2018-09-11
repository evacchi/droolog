package org.drools.droolog.processor;

import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import org.drools.droolog.meta.lib.AbstractStructure;

public class MetaStructureProcessor extends AbstractClassProcessor {

    public MethodDeclaration factoryMethodDeclaration() {
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        return new MethodDeclaration(
                publicM,
                JavaParser.parseType("Structure"),
                "createStructure").setBody(new BlockStmt(new NodeList<>(new ReturnStmt(new NullLiteralExpr()))));
    }


    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        ClassOrInterfaceType extendedClass =
                JavaParser.parseClassOrInterfaceType(AbstractStructure.class.getCanonicalName())
                        .setTypeArguments(new TypeParameter(annotatedClassName + "ObjectTerm"));

        return super.classDeclaration(el)
                .setName("Structure")
                .setExtendedTypes(
                        new NodeList<>(extendedClass))
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .setMembers(members());
    }

    private NodeList<BodyDeclaration<?>> members() {
        EnumSet<Modifier> pub = EnumSet.of(Modifier.PUBLIC);
        ConstructorDeclaration cons = new ConstructorDeclaration(pub, "Structure")
                .setParameters(new NodeList<>(new Parameter(JavaParser.parseType("org.drools.droolog.meta.lib.Term[]"), "terms")))
                .setBody(new BlockStmt(
                        new NodeList<>(
                                new ExpressionStmt(new MethodCallExpr("super", new NameExpr("terms")))
                        )
                ));

        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new NullLiteralExpr())));

        Type t = JavaParser.parseType("org.drools.droolog.meta.lib.Term.Meta<?,?,?>");
        MethodDeclaration getValue = new MethodDeclaration(
                pub,
                t,
                "meta")
                .setBody(body);
        return new NodeList<>(cons, getValue);
    }
}
