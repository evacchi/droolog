package org.drools.droolog.processor;

import java.util.Collection;
import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.VoidType;
import org.drools.droolog.meta.lib.AbstractAtom;

import static java.util.Arrays.asList;

public class MetaAtomProcessor extends AbstractClassProcessor {

    public MethodDeclaration factoryMethodDeclaration() {
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        return new MethodDeclaration(
                publicM,
                JavaParser.parseType("Atom"),
                "createAtom").setBody(new BlockStmt(new NodeList<>(new ReturnStmt(new NullLiteralExpr()))));
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        ClassOrInterfaceType extendedClass =
                JavaParser.parseClassOrInterfaceType(AbstractAtom.class.getCanonicalName())
                        .setTypeArguments(new TypeParameter(annotatedClassName + "ObjectTerm"));

        return super.classDeclaration(el).setExtendedTypes(
                new NodeList<>(extendedClass))
                .setName("Atom")
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .setMembers(members());
    }

    protected NodeList<BodyDeclaration<?>> members() {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new NullLiteralExpr())));

        Type t = JavaParser.parseType("Object");
        MethodDeclaration getValue = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                t,
                "getValue")
                .setBody(body);
        MethodDeclaration setValue = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                "setValue",
                new VoidType(),
                new NodeList<>(new Parameter(t, "value")));

        return new NodeList<>(getValue, setValue);
    }

    protected String className(String annotatedClassName) {
        return "Atom";
    }
}
