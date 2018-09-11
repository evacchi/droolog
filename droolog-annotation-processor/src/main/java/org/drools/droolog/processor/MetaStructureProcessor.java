package org.drools.droolog.processor;

import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import org.drools.droolog.meta.lib.AbstractStructure;

public class MetaStructureProcessor extends AbstractClassProcessor {

    final static ClassOrInterfaceType StructureT =
            JavaParser.parseClassOrInterfaceType("Structure");

    public MethodDeclaration factoryMethodDeclaration() {
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        return new MethodDeclaration(
                publicM,
                StructureT,
                "createStructure")
                .setBody(new BlockStmt(
                        new NodeList<>(new ReturnStmt(
                                new ObjectCreationExpr(
                                        null, StructureT, new NodeList<>())))));
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        ClassOrInterfaceType extendedClass =
                JavaParser.parseClassOrInterfaceType(AbstractStructure.class.getCanonicalName())
                        .setTypeArguments(new TypeParameter(annotatedClassName + "ObjectTerm"));

        return super.classDeclaration(el)
                .setName("Structure")
                .setExtendedTypes(
                        new NodeList<>(extendedClass))
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        EnumSet<Modifier> pub = EnumSet.of(Modifier.PUBLIC);
        List<VariableElement> fields = ElementFilter.fieldsIn(el.getEnclosedElements());

        String metaName = el.getSimpleName().toString() + "Meta";
        Type t = JavaParser.parseType(metaName);
        FieldAccessExpr metaSingleton = new FieldAccessExpr(new NameExpr(metaName), "Instance");
        ConstructorDeclaration cons = makeConstructor(fields, metaSingleton);
        MethodDeclaration getValue = makeMetaGetter(pub, t, metaSingleton);
        return new NodeList<>(cons, getValue);
    }

    private MethodDeclaration makeMetaGetter(EnumSet<Modifier> pub, Type t, FieldAccessExpr metaSingleton) {
        BlockStmt body = new BlockStmt(new NodeList<>(new ReturnStmt(metaSingleton)));
        return new MethodDeclaration(pub, t, "meta").setBody(body);
    }

    private ConstructorDeclaration makeConstructor(List<VariableElement> fields, FieldAccessExpr metaSingleton) {
        MethodCallExpr createAtom = new MethodCallExpr(
                metaSingleton,
                "createAtom");

        NodeList<Expression> nodes = new NodeList<>();
        for (int i = 0; i <= fields.size(); i++) {
            nodes.add(createAtom);
        }

        return new ConstructorDeclaration(EnumSet.of(Modifier.PUBLIC), "Structure")
                .setBody(new BlockStmt(
                        new NodeList<>(
                                new ExpressionStmt(
                                        new MethodCallExpr("super",
                                                           new ArrayCreationExpr()
                                                                   .setElementType("org.drools.droolog.meta.lib.Term[]")
                                                                   .setInitializer(new ArrayInitializerExpr(nodes))))
                        )
                ));
    }
}
