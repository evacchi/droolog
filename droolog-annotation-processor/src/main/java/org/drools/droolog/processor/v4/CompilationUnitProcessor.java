package org.drools.droolog.processor.v4;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class CompilationUnitProcessor {

    protected final Filer f;

    public CompilationUnitProcessor(Filer f) {
        this.f = f;
    }

    public void process(Element el, String packageName, ClassOrInterfaceDeclaration cls) {
        CompilationUnit cu = compilationUnit(packageName, cls);
        write(cu, el, packageName, cls.getNameAsString());
    }

    protected CompilationUnit compilationUnit(String packageName, ClassOrInterfaceDeclaration clss) {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration(packageName);

        cu.getTypes().add(clss);
        return cu;
    }

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
