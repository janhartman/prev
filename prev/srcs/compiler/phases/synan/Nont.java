package compiler.phases.synan;

/**
 * CFG nonterminals.
 * 
 * @author sliva
 *
 */
public enum Nont {

	Source,

	Expr, ExprWhere, ExprWhere0, ExprAssign, ExprMulti, ExprMulti0,



	Type,

	Stmt, StmtMulti, StmtMulti0, StmtElse,

	Decl, DeclMulti, DeclMulti0,

	IdenTypeMulti, IdenTypeMulti0,

	IdenExprMulti, IdenExprMulti0

}
