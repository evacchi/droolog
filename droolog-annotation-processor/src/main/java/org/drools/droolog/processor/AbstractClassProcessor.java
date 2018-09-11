package org.drools.droolog.processor;

import java.util.EnumSet;

import javax.lang.model.element.Element;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public abstract class AbstractClassProcessor {
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        return new ClassOrInterfaceDeclaration()
                .setModifiers(EnumSet.of(Modifier.PUBLIC));
    }
}
