package compiler.phases.seman;

import common.report.Report;
import compiler.phases.abstr.AbsFullVisitor;
import compiler.phases.abstr.abstree.*;


public class RecordNamer extends AbsFullVisitor<Object, Object> {

    public Object visit(AbsArgs args, Object visArg) {
        for (AbsExpr arg : args.args())
            arg.accept(this, visArg);
        return null;
    }

    public Object visit(AbsArrExpr arrExpr, Object visArg) {
        arrExpr.index.accept(this, visArg);
        return arrExpr.array.accept(this, visArg);
    }

    public Object visit(AbsArrType arrType, Object visArg) {
        return arrType.elemType.accept(this, visArg);
    }

    public Object visit(AbsAssignStmt assignStmt, Object visArg) {
        assignStmt.dst.accept(this, visArg);
        assignStmt.src.accept(this, visArg);
        return null;
    }

    public Object visit(AbsAtomExpr atomExpr, Object visArg) {
        return atomExpr.type;
    }

    public Object visit(AbsAtomType atomType, Object visArg) {
        return atomType;
    }

    public Object visit(AbsBinExpr binExpr, Object visArg) {
        binExpr.fstExpr.accept(this, visArg);
        binExpr.sndExpr.accept(this, visArg);
        return null;
    }

    public Object visit(AbsCastExpr castExpr, Object visArg) {
        castExpr.expr.accept(this, visArg);
        return castExpr.type.accept(this, visArg);
    }

    public Object visit(AbsCompDecl compDecl, Object visArg) {
        return null;
    }

    public Object visit(AbsCompDecls compDecls, Object visArg) {
        return null;
    }

    public Object visit(AbsDecls decls, Object visArg) {
        for (AbsDecl decl : decls.decls())
            decl.accept(this, visArg);
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

    public Object visit(AbsFunName funName, Object visArg) {
        funName.args.accept(this, visArg);
        return null;
    }

    public Object visit(AbsIfStmt ifStmt, Object visArg) {
        ifStmt.cond.accept(this, visArg);
        ifStmt.thenBody.accept(this, visArg);
        ifStmt.elseBody.accept(this, visArg);
        return null;
    }

    public Object visit(AbsNewExpr newExpr, Object visArg) {
        return newExpr.type.accept(this, visArg);
    }

    public Object visit(AbsParDecl parDecl, Object visArg) {
        return null;
    }

    public Object visit(AbsParDecls pars, Object visArg) {
        return null;
    }

    public Object visit(AbsPtrType ptrType, Object visArg) {
        return ptrType.subType.accept(this, visArg);
    }

    public Object visit(AbsRecExpr recExpr, Object visArg) {
        Object raw_type = recExpr.record.accept(this, visArg);
        if (!(raw_type instanceof AbsRecType)) throw new Report.Error(recExpr.record.location(),
                "Internal Error -> Not a record type, but this would have been resolved in the TypeChecker");
        AbsRecType type = (AbsRecType) raw_type;
        try {
            AbsDecl compDecl = SemAn.recSymbTable().get(type).fnd(recExpr.comp.name);
            SemAn.declAt().put(recExpr.comp, compDecl);
            return compDecl.type.accept(this, visArg);
        } catch (SymbTable.CannotFndNameException e) {
            throw new Report.Error(recExpr.comp.location(), "CompName:" + recExpr.comp.name + " not found in the symbol table");
        }
    }

    public Object visit(AbsRecType recType, Object visArg) {
        return recType;
    }

    public Object visit(AbsStmtExpr stmtExpr, Object visArg) {
        //stmtExpr.stmts.accept(this,visArg);
        return stmtExpr.expr.accept(this, visArg);
    }

    public Object visit(AbsStmts stmts, Object visArg) {
        for (AbsStmt stmt : stmts.stmts())
            stmt.accept(this, visArg);
        return null;
    }

    public Object visit(AbsTypeDecl typeDecl, Object visArg) {
        return null;
    }

    public Object visit(AbsTypeName typeName, Object visArg) {
        AbsType type = (AbsType) typeName;
        while (type instanceof AbsTypeName)
            type = SemAn.declAt().get((AbsTypeName) type).type;
        return type.accept(this, visArg);
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

    public Object visit(AbsWhileStmt whileStmt, Object visArg) {
        whileStmt.cond.accept(this, visArg);
        whileStmt.body.accept(this, visArg);
        return null;
    }

}
