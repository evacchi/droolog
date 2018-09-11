package org.drools.droolog.processor;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import static java.util.stream.Collectors.toList;

public class ObjectProcessor extends AbstractClassProcessor {

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        return super.classDeclaration(el)
                .setName(annotatedClassName + "Object")
                .setExtendedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(annotatedClassName)))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        NodeList<BodyDeclaration<?>> bodyDeclarations = new NodeList<>();
        List<FieldProcessor> fields = ElementFilter.fieldsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());

        for (FieldProcessor fp : fields) {
            bodyDeclarations.add(fp.getter());
            bodyDeclarations.add(fp.setter());
        }

        return bodyDeclarations;
    }
}
