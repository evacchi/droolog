package org.drools.droolog.processor.v1;

import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.droolog.meta.lib.v1.AbstractTerm;
import org.drools.droolog.meta.lib.v1.Term;

public class MetaVariableProcessor extends AbstractClassProcessor {

    final static ClassOrInterfaceType VariableT = JavaParser.parseClassOrInterfaceType("Variable");
    final static ClassOrInterfaceType Term_VariableT = JavaParser.parseClassOrInterfaceType(Term.Variable.class.getCanonicalName());
    final static ClassOrInterfaceType AbstractTermT = JavaParser.parseClassOrInterfaceType(AbstractTerm.class.getCanonicalName());

    public MethodDeclaration factoryMethodDeclaration() {
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        return new MethodDeclaration(
                publicM,
                VariableT,
                "createVariable")
                .setBody(
                        new BlockStmt(new NodeList<>(new ReturnStmt(
                                new ObjectCreationExpr(null, VariableT, new NodeList<>())))));
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        return super.classDeclaration(el)
                .setName("Variable")
                .setImplementedTypes(new NodeList<>(Term_VariableT))
                .setExtendedTypes(new NodeList<>(AbstractTermT))
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC));
    }
}
