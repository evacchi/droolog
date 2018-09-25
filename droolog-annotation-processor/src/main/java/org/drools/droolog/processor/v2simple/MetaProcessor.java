package org.drools.droolog.processor.v2simple;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.droolog.meta.lib.v2simple.Term;

import static com.github.javaparser.ast.Modifier.FINAL;
import static com.github.javaparser.ast.Modifier.PRIVATE;
import static com.github.javaparser.ast.Modifier.PUBLIC;
import static com.github.javaparser.ast.Modifier.STATIC;
import static java.util.stream.Collectors.toList;

public class MetaProcessor {

    private static final Type TermArrayT = JavaParser.parseType(Term.class.getCanonicalName() + "[]");
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
        return new NodeList<BodyDeclaration<?>>(
                singletonField(el),
                classAtomField(el),
                structureFactory(el),
                classFactory(el),
                structureConverter(el)
        );
    }

    private FieldDeclaration singletonField(Element el) {
        String metaName = metaName(el);

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

        ClassOrInterfaceType atomT = atom().setTypeArguments(ClassT);
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
        String interfaceName = el.getSimpleName().toString();
        String className = el.getSimpleName().toString();
        MethodDeclaration md = new MethodDeclaration(
                EnumSet.of(PUBLIC),
                new ClassOrInterfaceType(null, interfaceName),
                "of");
        List<FieldProcessor> fields = fieldProcessorsFor(el);

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
        String interfaceName = el.getSimpleName().toString();
        String className = el.getSimpleName().toString();
        ClassOrInterfaceType ObjectT = new ClassOrInterfaceType(null, className);
        ClassOrInterfaceType InterfaceT = new ClassOrInterfaceType(null, interfaceName);
        ClassOrInterfaceType StructureT = structure().setTypeArguments(InterfaceT);

        String structureVar = "structure";
        NameExpr structureVarExpr = new NameExpr(structureVar);

        MethodDeclaration md = new MethodDeclaration(
                EnumSet.of(PUBLIC),
                "of",
                InterfaceT,
                new NodeList<>(new Parameter(StructureT, structureVar)));

        List<FieldProcessor> fields = fieldProcessorsFor(el);

        String termsVar = "terms";
        VariableDeclarationExpr termsVarDecl = new VariableDeclarationExpr(
                new VariableDeclarator(TermArrayT,
                                       termsVar,
                                       new MethodCallExpr(structureVarExpr, termsVar)));

        md.setBody(new BlockStmt(new NodeList<>(
                new ExpressionStmt(termsVarDecl),
                new ReturnStmt(new ObjectCreationExpr(null, ObjectT, valuesFromTerms(fields, termsVar)))
        )));
        return md;
    }

    private MethodDeclaration structureConverter(Element el) {
        String interfaceName = el.getSimpleName().toString();
        ClassOrInterfaceType StructureT = structure()
                .setTypeArguments(new ClassOrInterfaceType(null, interfaceName));
        MethodDeclaration md = new MethodDeclaration(
                EnumSet.of(PUBLIC), StructureT, "of");

        ArrayInitializerExpr array = new ArrayInitializerExpr();
        NodeList<Parameter> parameters = md.getParameters();
        NodeList<Expression> values = array.getValues();

        values.add(new NameExpr("ClassAtom"));
        for (FieldProcessor fp : fieldProcessorsFor(el)) {
            parameters.add(new Parameter(term().setTypeArguments(fp.type()), fp.name()));
            values.add(fp.nameExpr());
        }

        ArrayCreationExpr arrayCreationExpr =
                new ArrayCreationExpr(TermArrayT)
                        .setInitializer(array);

        md.setBody(new BlockStmt(new NodeList<>(
                new ReturnStmt(new ObjectCreationExpr().setType(StructureT).setArguments(
                        new NodeList<>(arrayCreationExpr))))));
        return md;
    }

    private NodeList<Expression> valuesFromTerms(List<FieldProcessor> fields, String termsVar) {
        NodeList<Expression> parameters = new NodeList<>();
        for (int i = 0; i < fields.size(); i++) {
            FieldProcessor field = fields.get(i);
            ArrayAccessExpr arrayAccessExpr = arrayAccess(termsVar, i);
            if (field.isStructure()) {
                parameters.add(valueOfStructure(field, arrayAccessExpr));
            } else {
                parameters.add(valueOfAtom(field, arrayAccessExpr));
            }
        }
        return parameters;
    }

    private Expression valueOfStructure(FieldProcessor field, ArrayAccessExpr arrayAccessExpr) {
        return new MethodCallExpr(
                new FieldAccessExpr(new NameExpr(metaName(field.type().asString())), "Instance"),
                "of",
                new NodeList<>(castTo(structure().setTypeArguments(field.type()), arrayAccessExpr)));
    }

    private EnclosedExpr castTo(Type type, Expression expr) {
        return new EnclosedExpr(new CastExpr(type, expr));
    }

    private MethodCallExpr valueOfAtom(FieldProcessor field, Expression arrayAccessExpr) {
        return new MethodCallExpr(
                castTo(atom().setTypeArguments(field.type()), arrayAccessExpr),
                "value");
    }

    private ArrayAccessExpr arrayAccess(String termsVar, int i) {
        return new ArrayAccessExpr(new NameExpr(termsVar), new IntegerLiteralExpr(i + 1));
    }

    private List<FieldProcessor> fieldProcessorsFor(Element el) {
        return ElementFilter.fieldsIn(el.getEnclosedElements())
                .stream().map(FieldProcessor::new).collect(toList());
    }

    private ClassOrInterfaceType term() {
        return new ClassOrInterfaceType(null, Term.class.getCanonicalName());
    }

    private ClassOrInterfaceType structure() {
        return new ClassOrInterfaceType(null, Term.Structure.class.getCanonicalName());
    }

    private ClassOrInterfaceType atom() {
        return new ClassOrInterfaceType(null, Term.Atom.class.getCanonicalName());
    }
}
