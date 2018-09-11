package org.drools.droolog.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.ast.body.BodyDeclaration;

import static java.util.stream.Collectors.toList;

public class ObjectProcessor extends AbstractCompilationUnitProcessor {

    public ObjectProcessor(Filer f) {
        super(f);
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
}
