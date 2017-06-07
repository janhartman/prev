package compiler.phases.synan;

/**
 * CFG nonterminals.
 *
 * @author sliva
 */
public enum Nont {

    Source,

    Expr, ExprWhere, ExprWhere0, ExprAssign, ExprMulti, ExprMulti0,

    Expr0, Expr1, Expr10, Expr2, Expr20, Expr3, Expr30, Expr4, Expr40, Expr5, Expr6, Expr60, Expr7,

    Type,

    Stmt, Stmt0, StmtMulti, StmtMulti0, StmtElse,

    Decl, DeclMulti, DeclMulti0,

    IdenTypeMulti, IdenTypeMulti0,

    IdenExprMulti, IdenExprMulti0

}
