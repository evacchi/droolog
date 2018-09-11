package org.drools.droolog.processor;

import java.util.Collection;
import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public abstract class AbstractClassProcessor {
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String className = className(annotatedClassName);

        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration(EnumSet.of(Modifier.PUBLIC), false, className);
        cls.getMembers().addAll(members(el));

        return cls;
    }
    protected abstract String className(String annotatedClassName);

    abstract protected Collection<BodyDeclaration<?>> members(Element el);


}
