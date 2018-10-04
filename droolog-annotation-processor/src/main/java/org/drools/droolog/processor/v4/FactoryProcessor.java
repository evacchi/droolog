package org.drools.droolog.processor.v4;

import java.util.EnumSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleTypeVisitor8;

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
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import org.drools.droolog.meta.lib.v4.ArrayOps;
import org.drools.droolog.meta.lib.v4.Factory;
import org.drools.droolog.meta.lib.v4.ObjectTerm;
import org.drools.droolog.meta.lib.v4.TermState;

import static com.github.javaparser.ast.Modifier.PUBLIC;
import static java.util.stream.Collectors.toList;

public class FactoryProcessor {

    public static String factoryName(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        return factoryName(annotatedClassName);
    }

    private static String factoryName(String annotatedClassName) {
        return annotatedClassName + "Factory";
    }

    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String metaName = factoryName(el);
        return new ClassOrInterfaceDeclaration()
                .setModifiers(EnumSet.of(PUBLIC))
                .setName(metaName)
                .setImplementedTypes(new NodeList<>(
                        new ClassOrInterfaceType(Factory.class.getCanonicalName())
                                .setTypeArguments(new NodeList<>(new ClassOrInterfaceType(el.getSimpleName().toString())))))
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        NodeList<BodyDeclaration<?>> nodes = new NodeList<>(
                makeField(el),
                makeValues(el),
                makeFactory(el)
        );

        return nodes;
    }

    private BodyDeclaration<?> makeFactory(Element el) {
        String name = el.getSimpleName().toString();
        Expression expr = el.asType().accept(new ConstructorTypeVisitor(), 0);
        return new MethodDeclaration(EnumSet.of(Modifier.PUBLIC), new ClassOrInterfaceType(name), "create")
                .setParameters(new NodeList<>(
                        new Parameter(new ClassOrInterfaceType("Object"), "args").setVarArgs(true)
                ))
                .setBody(
                        new BlockStmt(
                                new NodeList<>(
                                        new ReturnStmt(
                                                expr
                                        )
                                )
                        )
                );
    }

    private static Expression visitConstructor(String name, Element el, int offset) {
        NodeList<Expression> parameters = new NodeList<>();
        List<? extends VariableElement> ps = ElementFilter.fieldsIn(el.getEnclosedElements()).stream().filter(v -> !v.getSimpleName().contentEquals("meta")).collect(toList());
        int count = offset;
        for (VariableElement v : ps) {
            int off = offset + ps.size();
            Expression r = v.asType().accept(new ConstructorTypeVisitor(), off);
            if (r == null) {
                parameters.add(
                        new CastExpr(
                                new ClassOrInterfaceType(v.asType().toString()),
                                new ArrayAccessExpr(new NameExpr("args"), new IntegerLiteralExpr(count++))));
            } else {
                parameters.add(r);
                count++;
            }
        }

        return new ObjectCreationExpr(null, new ClassOrInterfaceType(name), parameters);
    }

    private static class ConstructorTypeVisitor extends SimpleTypeVisitor8<Expression, Integer> {

        @Override
        public Expression visitDeclared(DeclaredType t, Integer count) {
            if (t.asElement().getAnnotation(ObjectTerm.class) == null) {
                return null;
            }
            return visitConstructor(t.toString(), t.asElement(), count);
        }
    }

    private BodyDeclaration<?> makeValues(Element el) {
        String name = el.getSimpleName().toString();
        String metaName = factoryName(el);
        NodeList<Expression> arrays = new NodeList<>();
        NodeList<Expression> values = new NodeList<>();
        MethodCallExpr firstArray = new MethodCallExpr(new NameExpr(ArrayOps.class.getCanonicalName()), "of", values);
        arrays.add(firstArray);
        List<FieldProcessor> fields = fieldProcessorsFor(el);
        for (FieldProcessor fp : fields) {
            if (fp.name().equals("meta")) {
                continue;
            }
            if (fp.isStructure()) {
                MethodCallExpr mc = new MethodCallExpr(new FieldAccessExpr(new NameExpr(factoryName(fp.element().asType().toString())), "Instance"), "values", new NodeList<>(new MethodCallExpr(new NameExpr("o"), fp.name())));
                arrays.add(mc);
                values.add(new NullLiteralExpr());
            } else {
                values.add(new MethodCallExpr(new NameExpr("o"), fp.name()));
            }
        }
        return new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                "values",
                new ClassOrInterfaceType("Object[]"),
                new NodeList<>(new Parameter(new ClassOrInterfaceType("Object"), "object"))
        ).setBody(
                new BlockStmt(
                        new NodeList<>(
                                new ExpressionStmt(new VariableDeclarationExpr(
                                        new VariableDeclarator(new ClassOrInterfaceType(name), "o", new CastExpr(new ClassOrInterfaceType(name), new NameExpr("object"))))),
                                new ReturnStmt(
                                        new MethodCallExpr(new NameExpr(ArrayOps.class.getCanonicalName()), "concat", arrays)
                                )
                        )
                )
        );
    }

    private FieldDeclaration makeIndex(Element el) {
        NodeList<VariableDeclarator> fieldDeclarations = new NodeList<>();
        List<FieldProcessor> fps = fieldProcessorsFor(el);
        int count = 0;
        for (FieldProcessor fp : fps) {
            if (fp.type().asString().equals(factoryName(el))) {
                continue;
            }
            fieldDeclarations.add(new VariableDeclarator(PrimitiveType.intType(), fp.name(), new IntegerLiteralExpr(count)));
            count++;
        }
        fieldDeclarations.add(
                new VariableDeclarator(PrimitiveType.intType(), "$size", new IntegerLiteralExpr(count))
        );
        return new FieldDeclaration(
                EnumSet.of(Modifier.PUBLIC), fieldDeclarations)
                ;
    }

    private BodyDeclaration<?> makeTerms(Element el) {
        NodeList<Expression> nodes = new NodeList<>();
        for (FieldProcessor fp : fieldProcessorsFor(el)) {
            if (fp.type().asString().equals(factoryName(el))) {
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

    private BodyDeclaration<?> makeField(Element el) {
        String name = factoryName(el);
        ClassOrInterfaceType ClassT = new ClassOrInterfaceType(name);
        return new FieldDeclaration(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
                                    new VariableDeclarator(ClassT, "Instance", new ObjectCreationExpr(null, ClassT, new NodeList<>())));
    }

    private ConstructorDeclaration makeConstructor(Element el) {
        String name = el.getSimpleName().toString();
        ConstructorDeclaration cons = new ConstructorDeclaration(EnumSet.of(Modifier.PUBLIC), factoryName(el));
        cons.setParameters(new NodeList<>(new Parameter(new ClassOrInterfaceType(name), "o")))
                .getBody()
                .asBlockStmt()
                .addStatement(new AssignExpr(new NameExpr("object"), new NameExpr("o"), AssignExpr.Operator.ASSIGN));
        return cons;
    }
}
