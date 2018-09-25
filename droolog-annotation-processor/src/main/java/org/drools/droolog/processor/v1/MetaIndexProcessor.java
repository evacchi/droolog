package org.drools.droolog.processor.v1;

import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.type.PrimitiveType;

public class MetaIndexProcessor extends AbstractClassProcessor {

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        return super.classDeclaration(el).setExtendedTypes(
                new NodeList<>())
                .setName("Index")
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .setMembers(members(el));
    }

    protected NodeList<BodyDeclaration<?>> members(Element el) {
        List<VariableElement> fieldsIn = ElementFilter.fieldsIn(el.getEnclosedElements());
        NodeList<BodyDeclaration<?>> fields =
                new NodeList<>(
                        constant("$predicate", 0));
        for (int i = 0; i < fieldsIn.size(); i++) {
            VariableElement f = fieldsIn.get(i);
            fields.add(constant(f.getSimpleName().toString(), i + 1));
        }
        return fields;
    }

    private FieldDeclaration constant(String name, int value) {
        return new FieldDeclaration(
                EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
                new VariableDeclarator(new PrimitiveType(), name, new IntegerLiteralExpr(value)));
    }
}
