package org.drools.droolog.processor;

import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.drools.droolog.meta.lib.AbstractTerm;
import org.drools.droolog.meta.lib.Term;

public class MetaVariableProcessor extends AbstractClassProcessor {

    public MethodDeclaration factoryMethodDeclaration() {
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        return new MethodDeclaration(
                publicM,
                JavaParser.parseType("Variable"),
                "createVariable").setBody(new BlockStmt(new NodeList<>(new ReturnStmt(new NullLiteralExpr()))));
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();

        return super.classDeclaration(el)
                .setName("Variable")
                .setImplementedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(Term.Variable.class.getCanonicalName())))
                .setExtendedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(AbstractTerm.class.getCanonicalName())))
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC));
    }
}
