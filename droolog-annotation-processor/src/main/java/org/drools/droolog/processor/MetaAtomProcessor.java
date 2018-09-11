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
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.VoidType;
import org.drools.droolog.meta.lib.AbstractAtom;

public class MetaAtomProcessor extends AbstractClassProcessor {

    final static ClassOrInterfaceType AtomT = JavaParser.parseClassOrInterfaceType("Atom");

    public MethodDeclaration factoryMethodDeclaration() {
        EnumSet<Modifier> publicM = EnumSet.of(Modifier.PUBLIC);
        return new MethodDeclaration(
                publicM,
                AtomT,
                "createAtom").setBody(new BlockStmt(new NodeList<>(new ReturnStmt(
                new ObjectCreationExpr(null, AtomT, new NodeList<>())))));
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration(Element el) {
        String annotatedClassName = el.getSimpleName().toString();
        ClassOrInterfaceType extendedClass =
                JavaParser.parseClassOrInterfaceType(AbstractAtom.class.getCanonicalName())
                        .setTypeArguments(new TypeParameter(annotatedClassName + "ObjectTerm"));

        return super.classDeclaration(el).setExtendedTypes(
                new NodeList<>(extendedClass))
                .setName("Atom")
                .setModifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC))
                .setMembers(members(el));
    }

    protected NodeList<BodyDeclaration<?>> members(Element el) {
        Type t = JavaParser.parseType("Object");
        MethodDeclaration getValue = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                t,
                "getValue")
                .setBody(makeGetter(el));
        MethodDeclaration setValue = new MethodDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                "setValue",
                new VoidType(),
                new NodeList<>(new Parameter(t, "value")))
                .setBody(makeSetter(el));

        return new NodeList<>(getValue, setValue);
    }

    private BlockStmt makeSetter(Element el) {
        List<VariableElement> fields = ElementFilter.fieldsIn(el.getEnclosedElements());
        NodeList<SwitchEntryStmt> entries = new NodeList<>();
        for (int i = 0; i < fields.size(); i++) {
            entries.add(setterSwitchEntry(fields.get(i)));
        }

        entries.add(new SwitchEntryStmt(null, new NodeList<>(new ThrowStmt(
                new ObjectCreationExpr(
                        null,
                        JavaParser.parseClassOrInterfaceType(IllegalArgumentException.class.getCanonicalName()),
                        new NodeList<>())))));

        return new BlockStmt(new NodeList<>(
                new SwitchStmt(new MethodCallExpr(new ThisExpr(), "getIndex"), entries)));
    }

    private SwitchEntryStmt setterSwitchEntry(VariableElement f) {
        String fieldName = f.getSimpleName().toString();
        String fieldSetter = Fields.setterNameOf(fieldName);
        String t = f.asType().toString();
        Type tt = JavaParser.parseType(t);

        return new SwitchEntryStmt(
                new FieldAccessExpr(new NameExpr("Index"), fieldName),
                new NodeList<>(
                        new ExpressionStmt(new MethodCallExpr(
                                new MethodCallExpr(
                                        new ThisExpr(),
                                        "parentObject"),
                                fieldSetter,
                                new NodeList<>(new EnclosedExpr(new CastExpr(tt, new NameExpr("value")))))),
                        new ReturnStmt()
                ));
    }

    private BlockStmt makeGetter(Element el) {
        List<VariableElement> fields = ElementFilter.fieldsIn(el.getEnclosedElements());
        NodeList<SwitchEntryStmt> entries = new NodeList<>(
                getterSwitchEntry("$predicate", "getClass"));
        for (int i = 0; i < fields.size(); i++) {
            VariableElement f = fields.get(i);
            entries.add(getterSwitchEntry(f));
        }

        entries.add(new SwitchEntryStmt(null, new NodeList<>(new ThrowStmt(
                new ObjectCreationExpr(
                        null,
                        JavaParser.parseClassOrInterfaceType(IllegalArgumentException.class.getCanonicalName()),
                        new NodeList<>())))));

        return new BlockStmt(new NodeList<>(
                new SwitchStmt(new MethodCallExpr(new ThisExpr(), "getIndex"), entries)));
    }

    private SwitchEntryStmt getterSwitchEntry(VariableElement f) {
        String fieldName = f.getSimpleName().toString();
        String fieldGetter = Fields.getterNameOf(fieldName);

        return getterSwitchEntry(fieldName, fieldGetter);
    }

    private SwitchEntryStmt getterSwitchEntry(String fieldName, String fieldGetter) {
        return new SwitchEntryStmt(
                new FieldAccessExpr(new NameExpr("Index"), fieldName),
                new NodeList<>(new ReturnStmt(
                        new MethodCallExpr(
                                new MethodCallExpr(
                                        new ThisExpr(),
                                        "parentObject"),
                                fieldGetter))));
    }
}
