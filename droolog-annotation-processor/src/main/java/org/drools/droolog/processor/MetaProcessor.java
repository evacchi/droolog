package org.drools.droolog.processor;

import javax.lang.model.element.Element;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.droolog.meta.lib.AbstractMeta;

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
        return super.classDeclaration(el)
                .setName(annotatedClassName + "Meta")
                .setExtendedTypes(new NodeList<>(extended))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        MetaAtomProcessor metaAtomProcessor = new MetaAtomProcessor();
        MetaVariableProcessor metaVariableProcessor = new MetaVariableProcessor();
        MetaStructureProcessor metaStructureProcessor = new MetaStructureProcessor();
        return new NodeList<BodyDeclaration<?>>(
                metaAtomProcessor.factoryMethodDeclaration(),
                metaVariableProcessor.factoryMethodDeclaration(),
                metaStructureProcessor.factoryMethodDeclaration(),
                metaAtomProcessor.classDeclaration(el),
                metaVariableProcessor.classDeclaration(el),
                metaStructureProcessor.classDeclaration(el));
    }
}
