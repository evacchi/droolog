package org.drools.droolog.processor;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

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
                classFactory(el));
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
                new ClassOrInterfaceType(className),
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

}
