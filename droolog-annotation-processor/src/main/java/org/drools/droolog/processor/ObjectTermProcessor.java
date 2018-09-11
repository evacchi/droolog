package org.drools.droolog.processor;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;

import com.github.javaparser.ast.body.BodyDeclaration;

public class ObjectTermProcessor extends AbstractCompilationUnitProcessor {

    public ObjectTermProcessor(Filer f) {
        super(f);
    }

    @Override
    protected Collection<BodyDeclaration<?>> members(Element el) {
        return Collections.emptyList();
    }
}
