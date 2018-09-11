package org.drools.droolog.processor;

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

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("org.drools.droolog.meta.lib.ObjectTerm")
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
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element el : elements) {
                PackageElement enclosing =
                        (PackageElement) el.getEnclosingElement();
                String packageName = enclosing.getQualifiedName().toString();
                String annotatedClassName = el.getSimpleName().toString();
                String objectClassName = String.format("%s%s", annotatedClassName, "Object");

                new ObjectProcessor(f)
                        .process(el, packageName, annotatedClassName, objectClassName);

                String objectTermClassName = String.format("%s%s", annotatedClassName, "ObjectTerm");
                new ObjectTermProcessor(f)
                        .process(el, packageName, annotatedClassName, objectTermClassName);
            }
        }
        return false;
    }

}
