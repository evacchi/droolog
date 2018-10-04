package org.drools.droolog.processor.v4;

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

@SupportedAnnotationTypes("org.drools.droolog.meta.lib.v4.ObjectTerm")
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
        MetaProcessor metaProcessor = new MetaProcessor();
        FactoryProcessor factoryProcessor = new FactoryProcessor();

        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element el : elements) {
                Element enclosingElement = el.getEnclosingElement();
                if (!(enclosingElement instanceof PackageElement)) continue;
                PackageElement packageElement =
                        (PackageElement) enclosingElement;
                String packageName = packageElement.getQualifiedName().toString();

                ClassOrInterfaceDeclaration meta = metaProcessor
                        .classDeclaration(el);
                ClassOrInterfaceDeclaration factory = factoryProcessor
                        .classDeclaration(el);

                cup.process(el, packageName, meta);
                cup.process(el, packageName, factory);
            }
        }
        return false;
    }

}
