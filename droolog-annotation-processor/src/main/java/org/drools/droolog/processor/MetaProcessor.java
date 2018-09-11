package org.drools.droolog.processor;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.droolog.meta.lib.AbstractMeta;
import org.drools.droolog.meta.lib.AbstractStructure;
import org.drools.droolog.meta.lib.AbstractTerm;
import org.drools.droolog.meta.lib.Term;

public class MetaProcessor extends AbstractClassProcessor {

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        Type PersonMeta_Atom = JavaParser.parseType("PersonMeta.Atom");
        ClassOrInterfaceType extended = JavaParser.parseClassOrInterfaceType(AbstractMeta.class.getCanonicalName()).setTypeArguments(new NodeList<>(
                PersonMeta_Atom,
                JavaParser.parseType("PersonMeta.Variable"),
                JavaParser.parseType("PersonMeta.Structure")
        ));
        return super.classDeclaration(el).setExtendedTypes(new NodeList<>(extended));
    }

    @Override
    protected Collection<BodyDeclaration<?>> members(Element el) {
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        EnumSet<Modifier> publicStatic = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC);
        return Arrays.<BodyDeclaration<?>>asList(
                new MethodDeclaration(
                        publicM,
                        JavaParser.parseType("Atom"),
                        "createAtom").setBody(new BlockStmt(new NodeList<>(new ReturnStmt(new NullLiteralExpr())))),
                new MethodDeclaration(
                        publicM,
                        JavaParser.parseType("Variable"),
                        "createVariable").setBody(new BlockStmt(new NodeList<>(new ReturnStmt(new NullLiteralExpr())))),
                new MethodDeclaration(
                        publicM,
                        JavaParser.parseType("Structure"),
                        "createStructure").setBody(new BlockStmt(new NodeList<>(new ReturnStmt(new NullLiteralExpr())))),

                new MetaAtomProcessor().classDeclaration(el),
                new ClassOrInterfaceDeclaration(
                        publicM,
                        false,
                        "Variable")

                        .setImplementedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(Term.Variable.class.getCanonicalName())))
                        .setExtendedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(AbstractTerm.class.getCanonicalName()))),
                new MetaStructureProcessor().classDeclaration(el)

        );
    }

    protected String className(String annotatedClassName) {
        return annotatedClassName + "Meta";
    }
}
