package org.drools.droolog.processor.v3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import org.drools.droolog.meta.lib.Meta;
import org.drools.droolog.meta.lib4.TermState;

public class ObjectProcessor {

    static final String TermState = "state";

    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        return new ClassOrInterfaceDeclaration()
                .setModifiers(EnumSet.of(Modifier.PUBLIC))
                .setName(objectName(annotatedClassName))
//                .setImplementedTypes(new NodeList<>(JavaParser.parseClassOrInterfaceType(annotatedClassName)))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        String generatedClassName = objectName(el);

        NodeList<BodyDeclaration<?>> bodyDeclarations = new NodeList<>();
        List<FieldProcessor> fields = new ArrayList<>();
        int j = 0;
        for (VariableElement f : ElementFilter.fieldsIn(el.getEnclosedElements())) {
            if (f.getAnnotation(Meta.class) != null) {
                continue;
            }
            FieldProcessor fp = new FieldProcessor(f, j++);
            fields.add(fp);
        }

        fields.stream().map(FieldProcessor::index).forEach(bodyDeclarations::add);
        fields.stream().map(FieldProcessor::termState).forEach(bodyDeclarations::add);

        MethodDeclaration methodDeclaration = makeSelector(TermState, JavaParser.parseType(TermState.class.getCanonicalName()), fields);
        bodyDeclarations.add(methodDeclaration);

        return bodyDeclarations;
    }

    private MethodDeclaration makeSelector(String selector, Type type, List<FieldProcessor> fields) {
        NodeList<SwitchEntryStmt> switchEntryStmts = fields.stream().map(fp -> fp.switchEntry(selector)).collect(Collectors.toCollection(NodeList::new));
        switchEntryStmts.add(new SwitchEntryStmt().setStatements(new NodeList<>(new ThrowStmt(new ObjectCreationExpr(
                null,
                new ClassOrInterfaceType(null, "IllegalArgumentException"),
                new NodeList<>())))));
        return new MethodDeclaration(EnumSet.of(Modifier.PUBLIC), selector, type, new NodeList<>(
                new Parameter(PrimitiveType.intType(), "index")))
                .setBody(new BlockStmt(new NodeList<>(new SwitchStmt(
                        new NameExpr("index"),
                        switchEntryStmts))));
    }

    public static String objectName(Element el) {
        return objectName(el.getSimpleName().toString());
    }

    public static String objectName(String s) {
        return s + "_";
    }
}
