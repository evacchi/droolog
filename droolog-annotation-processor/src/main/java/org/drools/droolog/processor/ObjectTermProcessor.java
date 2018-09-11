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
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.drools.droolog.meta.lib.Term;

import static java.util.Arrays.asList;

public class ObjectTermProcessor extends AbstractClassProcessor {

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

        Type t = JavaParser.parseType(Term.Structure.class.getCanonicalName());
        MethodDeclaration getValue = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                t,
                "$getStructure")
                .setBody(body);
        MethodDeclaration setValue = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                "$setStructure",
                new VoidType(),
                new NodeList<>(new Parameter(
                        t,
                        "structure")));

        return new NodeList<>(getValue, setValue);
    }
}
