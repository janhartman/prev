package compiler.phases.seman;

import common.report.Report;
import compiler.phases.abstr.AbsFullVisitor;
import compiler.phases.abstr.abstree.*;


public class RecordNamer extends AbsFullVisitor<Object, Object> {

    public Object visit(AbsArrExpr arrExpr, Object visArg) {
        arrExpr.index.accept(this, visArg);
        return arrExpr.array.accept(this, visArg);
    }

    public Object visit(AbsArrType arrType, Object visArg) {
        return arrType.elemType.accept(this, visArg);
    }

    public Object visit(AbsAtomExpr atomExpr, Object visArg) {
        return atomExpr.type;
    }

    public Object visit(AbsAtomType atomType, Object visArg) {
        return atomType;
    }

    public Object visit(AbsCastExpr castExpr, Object visArg) {
        castExpr.expr.accept(this, visArg);
        return castExpr.type.accept(this, visArg);
    }

    public Object visit(AbsCompDecls compDecls, Object visArg) {
        return null;
    }

    public Object visit(AbsDelExpr delExpr, Object visArg) {
        return delExpr.expr.accept(this, visArg);
    }

    public Object visit(AbsExprStmt exprStmt, Object visArg) {
        return exprStmt.expr.accept(this, visArg);
    }

    public Object visit(AbsFunDecl funDecl, Object visArg) {
        funDecl.parDecls.accept(this, visArg);
        funDecl.type.accept(this, visArg);
        return null;
    }

    public Object visit(AbsFunDef funDef, Object visArg) {
        funDef.value.accept(this, visArg);
        return null;
    }

    public Object visit(AbsNewExpr newExpr, Object visArg) {
        return newExpr.type.accept(this, visArg);
    }


    public Object visit(AbsParDecls pars, Object visArg) {
        return null;
    }

    public Object visit(AbsPtrType ptrType, Object visArg) {
        return ptrType.subType.accept(this, visArg);
    }

    public Object visit(AbsRecExpr recExpr, Object visArg) {
        AbsRecType type = (AbsRecType) recExpr.record.accept(this, visArg);
        try {
            AbsDecl compDecl = SemAn.recSymbTable().get(type).fnd(recExpr.comp.name);
            SemAn.declAt().put(recExpr.comp, compDecl);
            return compDecl.type;
        } catch (SymbTable.CannotFndNameException e) {
            throw new Report.Error(recExpr.comp.location(), "Component" + recExpr.comp.name + " not found in the symbol table");
        }
    }

    public Object visit(AbsRecType recType, Object visArg) {
        return recType;
    }

    public Object visit(AbsStmtExpr stmtExpr, Object visArg) {
        stmtExpr.stmts.accept(this, visArg);
        return stmtExpr.expr.accept(this, visArg);
    }

    public Object visit(AbsTypeDecl typeDecl, Object visArg) {
        return null;
    }

    public Object visit(AbsTypeName typeName, Object visArg) {
        AbsType type = typeName;
        while (type instanceof AbsTypeName) {
            type = SemAn.declAt().get((AbsTypeName) type).type;
        }
        return type;
    }

    public Object visit(AbsUnExpr unExpr, Object visArg) {
        return unExpr.subExpr.accept(this, visArg);
    }

    public Object visit(AbsVarDecl varDecl, Object visArg) {
        return varDecl.type.accept(this, visArg);
    }

    public Object visit(AbsVarName varName, Object visArg) {
        return SemAn.declAt().get(varName).type.accept(this, visArg);
    }

}
