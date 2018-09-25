package org.drools.droolog.processor.v1;

import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.droolog.meta.lib.v1.AbstractMeta;

import static com.github.javaparser.ast.Modifier.FINAL;
import static com.github.javaparser.ast.Modifier.PUBLIC;
import static com.github.javaparser.ast.Modifier.STATIC;

public class MetaProcessor extends AbstractClassProcessor {

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String metaName = annotatedClassName + "Meta";
        ClassOrInterfaceType abstractMeta =
                JavaParser.parseClassOrInterfaceType(AbstractMeta.class.getCanonicalName())
                        .setTypeArguments(new NodeList<>(
                                JavaParser.parseType(metaName + ".Atom"),
                                JavaParser.parseType(metaName + ".Variable"),
                                JavaParser.parseType(metaName + ".Structure")));
        return super.classDeclaration(el)
                .setName(metaName)
                .setExtendedTypes(new NodeList<>(abstractMeta))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        MetaIndexProcessor metaIndexProcessor = new MetaIndexProcessor();
        MetaAtomProcessor metaAtomProcessor = new MetaAtomProcessor();
        MetaVariableProcessor metaVariableProcessor = new MetaVariableProcessor();
        MetaStructureProcessor metaStructureProcessor = new MetaStructureProcessor();
        ObjectTermProcessor objectTermProcessor = new ObjectTermProcessor();
        return new NodeList<BodyDeclaration<?>>(
                singletonField(el),
                objectTermProcessor.factoryMethodDeclaration(el),
                objectTermProcessor.termGetter(el),
                metaAtomProcessor.factoryMethodDeclaration(),
                metaVariableProcessor.factoryMethodDeclaration(),
                metaStructureProcessor.factoryMethodDeclaration(),
                metaIndexProcessor.classDeclaration(el),
                metaAtomProcessor.classDeclaration(el),
                metaVariableProcessor.classDeclaration(el),
                metaStructureProcessor.classDeclaration(el));
    }

    private FieldDeclaration singletonField(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String metaName = annotatedClassName + "Meta";

        ClassOrInterfaceType metaT = new ClassOrInterfaceType(null, metaName);
        return new FieldDeclaration(
                EnumSet.of(PUBLIC, STATIC, FINAL),
                new VariableDeclarator(
                        metaT,
                        "Instance",
                        new ObjectCreationExpr(null, metaT, new NodeList<>())));
    }
}
