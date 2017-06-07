package compiler.phases.abstr;

import common.report.Report;
import compiler.phases.abstr.abstree.*;

/**
 * An abstract visitor of the abstract syntax tree.
 *
 * @param <Result> The result the visitor produces.
 * @param <Arg>    The argument the visitor carries around.
 * @author sliva
 */
public interface AbsVisitor<Result, Arg> {

    public default Result visit(AbsArgs args, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsArrExpr arrExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsArrType arrType, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsAssignStmt assignStmt, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsAtomExpr atomExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsAtomType atomType, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsBinExpr binExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsCastExpr castExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsCompDecl compDecl, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsCompDecls compDecls, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsDecls decls, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsDelExpr delExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsExprStmt exprStmt, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsFunDecl funDecl, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsFunDef funDef, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsFunName funName, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsIfStmt ifStmt, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsNewExpr newExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsParDecl parDecl, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsParDecls absPars, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsPtrType ptrType, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsRecExpr recExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsRecType recType, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsStmtExpr stmtExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsStmts stmts, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsTypeDecl typeDecl, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsTypeName typeName, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsUnExpr unExpr, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsVarDecl varDecl, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsVarName varName, Arg visArg) {
        throw new Report.InternalError();
    }

    public default Result visit(AbsWhileStmt whileStmt, Arg visArg) {
        throw new Report.InternalError();
    }

    ;

    public default Result visit(AbsExprDecl exprDecl, Arg visArg) {
        throw new Report.InternalError();
    }

    ;
}
