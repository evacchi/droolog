package org.drools.droolog.processor.v4;

import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleTypeVisitor8;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import org.drools.droolog.meta.lib.v4.ArrayOps;
import org.drools.droolog.meta.lib.v4.Factory;
import org.drools.droolog.meta.lib.v4.Meta;
import org.drools.droolog.meta.lib.v4.ObjectTerm;
import org.drools.droolog.meta.lib.v4.TermState;

import static com.github.javaparser.ast.Modifier.PUBLIC;
import static java.util.stream.Collectors.toList;

public class MetaProcessor {

    private static final ClassOrInterfaceType ClassT = new ClassOrInterfaceType(null, "Class")
            .setTypeArguments(new ClassOrInterfaceType(null, "?"));

    public static String metaName(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        return metaName(annotatedClassName);
    }

    private static String metaName(String annotatedClassName) {
        return annotatedClassName + "Meta";
    }

    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String metaName = metaName(el);
        return new ClassOrInterfaceDeclaration()
                .setModifiers(EnumSet.of(PUBLIC))
                .setName(metaName)
                .setImplementedTypes(new NodeList<>(
                        new ClassOrInterfaceType(Meta.class.getCanonicalName())
                                .setTypeArguments(new NodeList<>(new ClassOrInterfaceType(el.getSimpleName().toString())))))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        NodeList<BodyDeclaration<?>> nodes = new NodeList<>(
                makeIndex(el),
                makeTerms(el),
                makeTermsMethod(el),
                makeFactoryMethod(el)
        );

        return nodes;
    }


    private FieldDeclaration makeIndex(Element el) {
        NodeList<VariableDeclarator> fieldDeclarations = new NodeList<>();
        List<FieldProcessor> fps = fieldProcessorsFor(el);
        int count = 0;
        for (FieldProcessor fp : fps) {
            if (fp.type().asString().equals(metaName(el))) {
                continue;
            }
            fieldDeclarations.add(new VariableDeclarator(PrimitiveType.intType(), fp.name(), new IntegerLiteralExpr(count)));
            count++;
        }
        fieldDeclarations.add(
                new VariableDeclarator(PrimitiveType.intType(), "$size", new IntegerLiteralExpr(count))
        );
        return new FieldDeclaration(
                EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), fieldDeclarations)
                ;
    }

    private BodyDeclaration<?> makeTerms(Element el) {
        NodeList<Expression> nodes = new NodeList<>();
        for (FieldProcessor fp : fieldProcessorsFor(el)) {
            if (fp.type().asString().equals(metaName(el))) {
                continue;
            }
            nodes.add(new FieldAccessExpr(
                    new NameExpr(TermState.class.getCanonicalName()), "Ground"));
        }
        return new FieldDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new VariableDeclarator(new ClassOrInterfaceType(PrimitiveType.intType().toString() + "[]"),
                                       "terms", new ArrayInitializerExpr(nodes)));
    }

    private static List<FieldProcessor> fieldProcessorsFor(Element el) {
        return ElementFilter.fieldsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());
    }


    private MethodDeclaration makeFactoryMethod(Element el) {
        String name = el.getSimpleName().toString();
        MethodDeclaration m = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                new ClassOrInterfaceType(Factory.class.getCanonicalName()).setTypeArguments(new NodeList<>(new ClassOrInterfaceType(name))),
                "factory");
        m.setBody(new BlockStmt().addStatement(
                new ReturnStmt(new FieldAccessExpr(new NameExpr(FactoryProcessor.factoryName(el)), "Instance"))));
        return m;
    }

    private MethodDeclaration makeTermsMethod(Element el) {
        String name = el.getSimpleName().toString();
        MethodDeclaration m = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                JavaParser.parseType("int[]"),
                "terms");
        m.setBody(new BlockStmt().addStatement(
                new ReturnStmt(new NameExpr("terms"))));
        return m;
    }


}
