package org.drools.droolog.processor;

import java.util.ArrayList;
import java.util.Collection;
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
        return super.classDeclaration(el).setExtendedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(annotatedClassName)));
    }

    protected Collection<BodyDeclaration<?>> members(Element el) {
        ArrayList<BodyDeclaration<?>> bodyDeclarations = new ArrayList<>();
        List<FieldProcessor> fields = ElementFilter.fieldsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());

        for (FieldProcessor fp : fields) {
            bodyDeclarations.add(fp.getter());
            bodyDeclarations.add(fp.setter());
        }

        return bodyDeclarations;
    }

    protected String className(String annotatedClassName) {
        return annotatedClassName + "Object";
    }
}
