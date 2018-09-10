package org.drools.droolog.processor;

import java.io.IOException;
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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.google.auto.service.AutoService;
import org.drools.droolog.meta.lib.ObjectTerm;

@SupportedAnnotationTypes("org.drools.droolog.meta.lib.ObjectTerm")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class DroologProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Messager m = processingEnv.getMessager();
        Filer f = processingEnv.getFiler();
        if (set.stream().anyMatch(te -> ((TypeElement) te).getQualifiedName().contentEquals("org.drools.droolog.meta.lib.ObjectTerm"))) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ObjectTerm.class);

            for (Element el : elements) {
                PackageElement enclosing =
                        (PackageElement) el.getEnclosingElement();
                String packageName = enclosing.getQualifiedName().toString();
                String annotatedClassName = el.getSimpleName().toString();
                String objectClassName = String.format("%s%s", annotatedClassName, "Object");

                createObject(f, el, packageName, annotatedClassName, objectClassName);

                String objectTermClassName = String.format("%s%s", annotatedClassName, "ObjectTerm");
                createInternalInterface(f, el, packageName, objectClassName, objectTermClassName);
            }
        }
        return false;
    }

    private void createInternalInterface(Filer f, Element el, String packageName, String objectClassName, String className) {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration(packageName);

        ClassOrInterfaceDeclaration cls =
                cu.addClass(className);
        cls.setExtendedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(objectClassName)));

        try {
            JavaFileObject sourceFile = f.createSourceFile(String.format("%s.%s", packageName, className), el);
            sourceFile.openWriter()
                    .append(cu.toString())
                    .close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void createObject(Filer f, Element el, String packageName, String annotatedClassName, String className) {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration(packageName);

        ClassOrInterfaceDeclaration cls =
                cu.addClass(className);
        cls.setExtendedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(annotatedClassName)));

        TypeElement typeElement = (TypeElement) el;
        for (Element v : typeElement.getEnclosedElements()) {
            if (v.getSimpleName().contentEquals("<init>")) continue;
            String fieldName = v.getSimpleName().toString();
            TypeMirror typeMirror = v.asType();

            Type fieldType = JavaParser.parseType(typeMirror.toString());
            cls.addMethod(
                    "get" + capitalized(fieldName), Modifier.PUBLIC)
                    .setType(fieldType)
                    .setBody(
                            new BlockStmt(new NodeList<>(new ReturnStmt(
                                    new FieldAccessExpr(new ThisExpr(), fieldName)))));

            cls.addMethod("set" + capitalized(fieldName), Modifier.PUBLIC)
                    .addParameter(fieldType, fieldName)
                    .setBody(
                            new BlockStmt(new NodeList<>(
                                    new ExpressionStmt(new AssignExpr(
                                            new FieldAccessExpr(new ThisExpr(), fieldName), new NameExpr(fieldName), AssignExpr.Operator.ASSIGN)))));
        }

        try {
            JavaFileObject sourceFile = f.createSourceFile(String.format("%s.%s", packageName, className), el);
            sourceFile.openWriter()
                    .append(cu.toString())
                    .close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String capitalized(String original) {
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
