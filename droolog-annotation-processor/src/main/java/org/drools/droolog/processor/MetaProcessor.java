package org.drools.droolog.processor;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.droolog.meta.lib2.Term;

import static com.github.javaparser.ast.Modifier.FINAL;
import static com.github.javaparser.ast.Modifier.PRIVATE;
import static com.github.javaparser.ast.Modifier.PUBLIC;
import static com.github.javaparser.ast.Modifier.STATIC;
import static java.util.stream.Collectors.toList;

public class MetaProcessor {

    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String metaName = annotatedClassName + "Meta";
        return new ClassOrInterfaceDeclaration()
                .setModifiers(EnumSet.of(PUBLIC))
                .setName(metaName)
                .setMembers(members(el));
    }

    private NodeList<BodyDeclaration<?>> members(Element el) {
        return new NodeList<BodyDeclaration<?>>(
                singletonField(el),
                classAtomField(el),
                structureFactory(el),
                classFactory(el),
                structureConverter(el)
        );
    }

    private FieldDeclaration singletonField(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String metaName = annotatedClassName + "Meta";

        ClassOrInterfaceType metaT = new ClassOrInterfaceType(null, metaName);
        return new FieldDeclaration(
                EnumSet.of(PUBLIC, STATIC, FINAL),
                new VariableDeclarator(
                        metaT,
                        "Instance",
                        new ObjectCreationExpr(null, metaT, new NodeList<>())));
    }

    private FieldDeclaration classAtomField(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String fieldName = "ClassAtom";

        ClassOrInterfaceType atomT =
                new ClassOrInterfaceType(null, "org.drools.droolog.meta.lib2.Term.Atom")
                        .setTypeArguments(
                                new ClassOrInterfaceType(null, "Class")
                                        .setTypeArguments(new ClassOrInterfaceType(null, "?"))
                        );
        return new FieldDeclaration(
                EnumSet.of(PRIVATE, STATIC, FINAL),
                new VariableDeclarator(
                        atomT,
                        fieldName,
                        new ObjectCreationExpr(null, atomT, new NodeList<>(
                                new ClassExpr(new ClassOrInterfaceType(null, annotatedClassName))
                        )).setDiamondOperator()));
    }

    private MethodDeclaration classFactory(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String className = el.getSimpleName().toString() + "Object";
        MethodDeclaration md = new MethodDeclaration(
                EnumSet.of(PUBLIC),
                new ClassOrInterfaceType(null, className),
                "of");
        NodeList<BodyDeclaration<?>> bodyDeclarations = new NodeList<>();
        List<FieldProcessor> fields = ElementFilter.methodsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());

        for (FieldProcessor fp : fields) {
            md.getParameters().add(fp.parameter());
        }
        md.setBody(new BlockStmt(new NodeList<>(
                new ReturnStmt(new ObjectCreationExpr().setType(className).setArguments(
                        md.getParameters()
                                .stream()
                                .map(NodeWithSimpleName::getNameAsExpression)
                                .collect(Collectors.toCollection(NodeList::new)))))));
        return md;
    }

    private MethodDeclaration structureFactory(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String className = el.getSimpleName().toString() + "Object";
        ClassOrInterfaceType ClassT = new ClassOrInterfaceType(null, className);
        ClassOrInterfaceType StructureT = new ClassOrInterfaceType(null, Term.Structure.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, className));
        MethodDeclaration md = new MethodDeclaration(
                EnumSet.of(PUBLIC),
                "of",
                ClassT,
                new NodeList<>(new Parameter(StructureT, "structure")));
        NodeList<BodyDeclaration<?>> bodyDeclarations = new NodeList<>();
        List<FieldProcessor> fields = ElementFilter.methodsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());

        ExpressionStmt terms = new ExpressionStmt(new VariableDeclarationExpr(
                new VariableDeclarator(JavaParser.parseType(Term.class.getCanonicalName() + "[]"),
                                       "terms",
                                       new MethodCallExpr(new NameExpr("structure"), "terms"))));

        NodeList<Statement> statements = new NodeList<>(terms);

        NodeList<Expression> parameters = new NodeList<>();
        int i = 0;
        for (FieldProcessor field : fields) {
            i++;
            String fieldName = field.getter().getNameAsString();
            ClassOrInterfaceType t = new ClassOrInterfaceType(null, Term.Atom.class.getCanonicalName()).setTypeArguments(field.getter().getType());
            parameters.add(
                    new MethodCallExpr(new EnclosedExpr(new CastExpr(t, new ArrayAccessExpr(new NameExpr("terms"), new IntegerLiteralExpr(i)))), "value"));
        }

//        statements.add(new ReturnStmt())

        statements.add(new ReturnStmt(new ObjectCreationExpr(null, ClassT, parameters)));

        md.setBody(new BlockStmt(statements));
        return md;
    }

    private MethodDeclaration structureConverter(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        String className = el.getSimpleName().toString() + "Object";
        ClassOrInterfaceType StructureT = new ClassOrInterfaceType(null, Term.Structure.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, className));
        MethodDeclaration md = new MethodDeclaration(
                EnumSet.of(PUBLIC),
                StructureT,
                "of");
        NodeList<BodyDeclaration<?>> bodyDeclarations = new NodeList<>();
        List<FieldProcessor> fields = ElementFilter.methodsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());

        for (FieldProcessor fp : fields) {
            Parameter p = fp.parameter();
            md.getParameters().add(
                    new Parameter(
                            new ClassOrInterfaceType(null, Term.class.getCanonicalName()).setTypeArguments(p.getType()),
                            p.getName()));
        }

        NodeList<Expression> parameters = new NodeList<>();
        parameters.add(new NameExpr("ClassAtom"));
        md.getParameters()
                .stream()
                .map(NodeWithSimpleName::getNameAsExpression)
                .forEach(parameters::add);
        ArrayInitializerExpr array = new ArrayInitializerExpr()
                .setValues(parameters);

        ArrayCreationExpr arrayCreationExpr = new ArrayCreationExpr(
                JavaParser.parseType(Term.class.getCanonicalName() + "[]"))
                .setInitializer(array);
        md.setBody(new BlockStmt(new NodeList<>(
                new ReturnStmt(new ObjectCreationExpr().setType(StructureT).setArguments(
                        new NodeList<>(arrayCreationExpr))))));
        return md;
    }

}
