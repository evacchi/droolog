package org.drools.droolog.processor.v1;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("org.drools.droolog.meta.lib.v1.ObjectTerm")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class DroologProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager m = processingEnv.getMessager();
        Filer f = processingEnv.getFiler();
        CompilationUnitProcessor cup = new CompilationUnitProcessor(f);
        ObjectProcessor objectProcessor = new ObjectProcessor();
        MetaProcessor metaProcessor = new MetaProcessor();
        ObjectTermProcessor objectTermProcessor = new ObjectTermProcessor();

        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element el : elements) {
                PackageElement enclosing =
                        (PackageElement) el.getEnclosingElement();
                String packageName = enclosing.getQualifiedName().toString();

                ClassOrInterfaceDeclaration object = objectProcessor
                        .classDeclaration(el);

                ClassOrInterfaceDeclaration meta = metaProcessor
                        .classDeclaration(el);

                ClassOrInterfaceDeclaration objectTerm = objectTermProcessor
                        .classDeclaration(el);

                cup.process(el, packageName, object);
                cup.process(el, packageName, meta);
                cup.process(el, packageName, objectTerm);
            }
        }
        return false;
    }

}
