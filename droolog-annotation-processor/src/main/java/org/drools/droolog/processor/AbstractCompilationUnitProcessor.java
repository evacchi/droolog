package org.drools.droolog.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import static java.util.stream.Collectors.toList;

public abstract class AbstractCompilationUnitProcessor {

    protected final Filer f;

    public AbstractCompilationUnitProcessor(Filer f) {
        this.f = f;
    }

    public void process(Element el, String packageName, String annotatedClassName, String className) {
        CompilationUnit cu = compilationUnit(el, packageName, annotatedClassName, className);
        write(cu, el, packageName, className);
    }

    protected CompilationUnit compilationUnit(Element el, String packageName, String annotatedClassName, String className) {

        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration(packageName);

        ClassOrInterfaceDeclaration cls =
                cu.addClass(className);
        cls.setExtendedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(annotatedClassName)));

        cls.getMembers().addAll(members(el));

        return cu;
    }

    abstract protected Collection<BodyDeclaration<?>> members(Element el) ;

    protected void write(CompilationUnit cu, Element el, String packageName, String className) {
        try {
            JavaFileObject sourceFile = f.createSourceFile(String.format("%s.%s", packageName, className), el);
            sourceFile.openWriter()
                    .append(cu.toString())
                    .close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
