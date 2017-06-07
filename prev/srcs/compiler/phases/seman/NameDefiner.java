package compiler.phases.seman;

import common.report.Report;
import compiler.phases.abstr.AbsVisitor;
import compiler.phases.abstr.abstree.*;

/**
 * A visitor that traverses (a part of) the AST and stores all declarations
 * encountered into the symbol table. It is meant to be called from another
 * visitor, namely {@link NameChecker}.
 *
 * @author sliva
 */
public class NameDefiner implements AbsVisitor<Object, Object> {

    /**
     * The symbol table.
     */
    private final SymbTable symbTable;

    /**
     * Constructs a new name definer using the specified symbol table.
     *
     * @param symbTable The symbol table.
     */
    public NameDefiner(SymbTable symbTable) {
        this.symbTable = symbTable;
    }

    /**
     * just declarations
     */

    public Object visit(AbsDecls node, Object visArg) {
        for (AbsDecl decl : node.decls()) {
            try {
                symbTable.ins(decl.name, decl);
            } catch (SymbTable.CannotInsNameException cine) {
                throw new Report.Error(decl.location(), "Name " + decl.name + " already declared");
            }
        }


        for (AbsDecl decl : node.decls()) {
            decl.accept(this, visArg);
        }
        return null;
    }

    public Object visit(AbsTypeDecl node, Object visArg) {
        node.type.accept((NameChecker) visArg, null);
        return null;
    }


    public Object visit(AbsVarDecl node, Object visArg) {
        node.type.accept((NameChecker) visArg, null);
        return null;
    }


    public Object visit(AbsFunDecl node, Object visArg) {
        node.type.accept((NameChecker) visArg, null);

        for (AbsParDecl parDecl : node.parDecls.parDecls()) {
            parDecl.type.accept((NameChecker) visArg, null);
        }

        return null;
    }


    public Object visit(AbsFunDef node, Object visArg) {
        node.type.accept((NameChecker) visArg, null);

        for (AbsParDecl parDecl : node.parDecls.parDecls()) {
            parDecl.type.accept((NameChecker) visArg, null);
        }

        symbTable.newScope();

        for (AbsParDecl parDecl : node.parDecls.parDecls()) {
            try {
                symbTable.ins(parDecl.name, parDecl);
            } catch (SymbTable.CannotInsNameException cine) {
                throw new Report.Error(node.location(), "Name " + node.name + " already declared");
            }
        }

        node.value.accept((NameChecker) visArg, null);

        symbTable.oldScope();
        return null;
    }

    public Object visit(AbsCompDecl node, Object visArg) {
        node.type.accept((NameChecker) visArg, null);
        return null;
    }

    public Object visit(AbsCompDecls node, Object visArg) {
        for (AbsCompDecl compDecl : node.compDecls()) {
            compDecl.accept(this, null);
        }
        return null;
    }


    public Object visit(AbsParDecl node, Object visArg) {
        return null;
    }

    public Object visit(AbsParDecls node, Object visArg) {
        return null;
    }

}
