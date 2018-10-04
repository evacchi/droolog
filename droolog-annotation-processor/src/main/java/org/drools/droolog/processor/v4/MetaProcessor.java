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
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import org.drools.droolog.meta.lib.v4.ArrayOps;
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
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        NodeList<BodyDeclaration<?>> nodes = new NodeList<>(
                makeIndex(el),
                makeField(el),
                makeTerms(el),
                makeConstructor(el),
                makeValues(el),
                makeFactory(el)
        );

        return nodes;
    }

    private BodyDeclaration<?> makeFactory(Element el) {
        String name = el.getSimpleName().toString();
        Expression expr = el.asType().accept(new ConstructorTypeVisitor(), 0);
        return new MethodDeclaration(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), new ClassOrInterfaceType(name), "create")
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
        String metaName = metaName(el);
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
                FieldAccessExpr fa = new FieldAccessExpr(new MethodCallExpr(new NameExpr("object"), fp.name()), "meta");
                MethodCallExpr mc = new MethodCallExpr(fa, "values");
                arrays.add(mc);
                values.add(new NullLiteralExpr());
            } else {
                values.add(new MethodCallExpr(new NameExpr("object"), fp.name()));
            }
        }
        return new MethodDeclaration(EnumSet.of(Modifier.PUBLIC), new ClassOrInterfaceType("Object[]"), "values").setBody(
                new BlockStmt(
                        new NodeList<>(
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

    private BodyDeclaration<?> makeField(Element el) {
        String name = el.getSimpleName().toString();
        return new FieldDeclaration(EnumSet.of(Modifier.PRIVATE, Modifier.FINAL),
                                    new VariableDeclarator(new ClassOrInterfaceType(name), "object"));
    }

    private ConstructorDeclaration makeConstructor(Element el) {
        String name = el.getSimpleName().toString();
        ConstructorDeclaration cons = new ConstructorDeclaration(EnumSet.of(Modifier.PUBLIC), metaName(el));
        cons.setParameters(new NodeList<>(new Parameter(new ClassOrInterfaceType(name), "o")))
                .getBody()
                .asBlockStmt()
                .addStatement(new AssignExpr(new NameExpr("object"), new NameExpr("o"), AssignExpr.Operator.ASSIGN));
        return cons;
    }
}
