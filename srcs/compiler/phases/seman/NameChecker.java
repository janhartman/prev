package compiler.phases.seman;

import common.report.Report;
import compiler.phases.abstr.AbsVisitor;
import compiler.phases.abstr.abstree.*;

/**
 * A visitor that traverses (a part of) the AST and checks if all names used are
 * visible where they are used. This visitor uses another visitor, namely
 * {@link NameDefiner}, whenever a declaration is encountered during the AST
 * traversal.
 *
 * @author sliva
 */
public class NameChecker implements AbsVisitor<Object, Object> {

    /**
     * The symbol table.
     */
    private final SymbTable symbTable;

    /**
     * The name definer.
     */
    private final NameDefiner nameDefiner;

    /**
     * Constructs a new name checker using the specified symbol table.
     *
     * @param symbTable The symbol table.
     */
    public NameChecker(SymbTable symbTable) {
        this.symbTable = symbTable;
        this.nameDefiner = new NameDefiner(symbTable);
    }


    /**
     * expressions
     */

    public Object visit(AbsArgs node, Object visArg) {
        for (AbsExpr expr : node.args()) {
            expr.accept(this, null);
        }
        return null;
    }


    public Object visit(AbsArrExpr node, Object visArg) {
        node.array.accept(this, null);
        node.index.accept(this, null);
        return null;
    }


    public Object visit(AbsAtomExpr node, Object visArg) {
        return null;
    }


    public Object visit(AbsBinExpr node, Object visArg) {
        node.fstExpr.accept(this, null);
        node.sndExpr.accept(this, null);
        return null;
    }


    public Object visit(AbsCastExpr node, Object visArg) {
        node.expr.accept(this, null);
        node.type.accept(this, null);
        return null;
    }


    public Object visit(AbsDelExpr node, Object visArg) {
        node.expr.accept(this, null);
        return null;
    }


    public Object visit(AbsNewExpr node, Object visArg) {
        node.type.accept(this, null);
        return null;
    }


    public Object visit(AbsRecExpr node, Object visArg) {
        node.record.accept(this, null);
        // do not visit node.comp - this check will be done in type checker
        return null;
    }


    public Object visit(AbsUnExpr node, Object visArg) {
        node.subExpr.accept(this, null);
        return null;
    }


    // variable access - check name
    public Object visit(AbsVarName node, Object visArg) {
        try {
            AbsDecl decl = symbTable.fnd(node.name);
            if (!(decl instanceof AbsVarDecl)) {
                throw new Report.Error(node.location(), "Name " + node.name + " used as variable name but not declared as a variable");
            }
            SemAn.declAt().put(node, decl);
        } catch (SymbTable.CannotFndNameException cfne) {
            throw new Report.Error(node.location(), "Variable " + node.name + " was not declared");
        }
        return null;
    }


    // function call - check name
    public Object visit(AbsFunName node, Object visArg) {
        try {
            AbsDecl decl = symbTable.fnd(node.name);
            if (!(decl instanceof AbsFunDef) && !(decl instanceof AbsFunDecl)) {
                throw new Report.Error(node.location(), "Name " + node.name + " used as function name but not declared as a function");
            }
            SemAn.declAt().put(node, decl);
        } catch (SymbTable.CannotFndNameException cfne) {
            throw new Report.Error(node.location(), "Function " + node.name + " was not declared");
        }

        node.args.accept(this, null);
        return null;
    }


    /**
     * statements
     */

    public Object visit(AbsAssignStmt node, Object visArg) {
        node.src.accept(this, null);
        node.dst.accept(this, null);
        return null;
    }


    public Object visit(AbsExprStmt node, Object visArg) {
        node.expr.accept(this, null);
        return null;
    }


    public Object visit(AbsIfStmt node, Object visArg) {
        node.cond.accept(this, null);
        node.thenBody.accept(this, null);
        node.elseBody.accept(this, null);
        return null;
    }


    public Object visit(AbsStmts node, Object visArg) {
        for (AbsStmt stmt : node.stmts()) {
            stmt.accept(this, null);
        }
        return null;
    }


    public Object visit(AbsWhileStmt node, Object visArg) {
        node.cond.accept(this, null);
        node.body.accept(this, null);
        return null;
    }

    // the only class to have declarations
    // initialize a new scope
    public Object visit(AbsStmtExpr node, Object visArg) {
        symbTable.newScope();
        node.decls.accept(this, null);
        node.stmts.accept(this, null);
        node.expr.accept(this, null);
        symbTable.oldScope();

        return null;
    }


    /**
     * types
     */

    public Object visit(AbsArrType node, Object visArg) {
        node.elemType.accept(this, null);
        node.len.accept(this, null);
        return null;
    }


    public Object visit(AbsAtomType node, Object visArg) {
        return null;
    }


    public Object visit(AbsPtrType node, Object visArg) {
        node.subType.accept(this, null);
        return null;
    }


    public Object visit(AbsRecType node, Object visArg) {
        node.compDecls.accept(this, null);
        return null;
    }

    // check if the type name is declared
    public Object visit(AbsTypeName node, Object visArg) {
        try {
            AbsDecl decl = symbTable.fnd(node.name);
            if (!(decl instanceof AbsTypeDecl)) {
                throw new Report.Error(node.location(), "Name " + node.name + " used as type name but not declared as a type");
            }
            SemAn.declAt().put(node, decl);
        } catch (SymbTable.CannotFndNameException cfne) {
            throw new Report.Error(node.location(), "Type " + node.name + " was not declared");
        }
        return null;
    }


    /**
     * declarations
     */

    public Object visit(AbsDecls node, Object visArg) {
        node.accept(this.nameDefiner, this);
        return null;
    }


    public Object visit(AbsCompDecl node, Object visArg) {
        node.type.accept(this, null);
        return null;
    }


    public Object visit(AbsCompDecls node, Object visArg) {
        for (AbsCompDecl compDecl : node.compDecls()) {
            compDecl.accept(this, null);
        }
        return null;
    }


}
