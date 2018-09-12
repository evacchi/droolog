package org.drools.droolog.processor;

import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;

import static java.util.stream.Collectors.toList;

public class ObjectProcessor {

    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        return new ClassOrInterfaceDeclaration()
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setName(annotatedClassName + "Object")
                .setImplementedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(annotatedClassName)))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        String generatedClassName = el.getSimpleName().toString() + "Object";

        NodeList<BodyDeclaration<?>> bodyDeclarations = new NodeList<>();
        List<FieldProcessor> fields = ElementFilter.methodsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());

        makeFields(bodyDeclarations, fields);
        bodyDeclarations.add(
                makeConstructor(fields, generatedClassName));
        makeGetters(bodyDeclarations, fields);

        return bodyDeclarations;
    }

    private void makeGetters(NodeList<BodyDeclaration<?>> bodyDeclarations, List<FieldProcessor> fields) {
        for (FieldProcessor fp : fields) {
            bodyDeclarations.add(fp.getter());
        }
    }

    private ConstructorDeclaration makeConstructor(List<FieldProcessor> fields, String generatedClassName) {
        ConstructorDeclaration ctor = new ConstructorDeclaration(generatedClassName);
        NodeList<Parameter> parameters = ctor.getParameters();
        NodeList<Statement> statements = ctor.getBody().asBlockStmt().getStatements();
        for (FieldProcessor fp : fields) {
            parameters.add(fp.parameter());
            statements.add(fp.assignment());
        }
        return ctor;
    }


    private void makeFields(NodeList<BodyDeclaration<?>> bodyDeclarations, List<FieldProcessor> fields) {
        for (FieldProcessor fp : fields) {
            bodyDeclarations.add(fp.field());
        }
    }

    private FieldDeclaration makeField(String fieldName, Type fieldType) {
        return new FieldDeclaration(
                EnumSet.of(Modifier.FINAL, Modifier.PRIVATE),
                fieldType,
                fieldName);
    }

    private MethodDeclaration makeGetter(String fieldName, Type fieldType) {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(
                new FieldAccessExpr(new ThisExpr(), fieldName))));
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                fieldType,
                fieldName)
                .setBody(body);
    }
}
