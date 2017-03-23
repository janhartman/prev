package compiler.phases.abstr;

import java.util.*;
import common.report.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.lexan.Term;
import compiler.phases.synan.*;
import compiler.phases.synan.dertree.*;

/**
 * Transforms a derivation tree into an abstract syntax tree.
 *
 * @author sliva
 *
 */
public class DerToAbsTree implements DerVisitor<AbsTree, AbsTree> {

    @Override
    public AbsTree visit(DerNode node, AbsTree visArg) {

        // empty node (ε)
        if (node.subtrees().size() == 0) {
            return visArg;
        }

        AbsTree subtree;
        String name;
        Location location;
        DerTree first = node.subtree(0);

        int idx1 = 999;
        int idx2 = 999;

        //assume visArg is never null in Expr*0
        switch(node.label) {

            // root node
            case Source:
                return first.accept(this, null);

            // expr → expr1 expr0
            case Expr:

            // expr1 → expr2 expr10
            case Expr1:

            // expr2 → expr3 expr20
            case Expr2:

            // expr3 → expr4 expr30
            case Expr3:

            // expr4 → expr5 expr40
            case Expr4:

            // expr6 → expr7 expr60
            case Expr6:
                subtree = first.accept(this, null);
                return node.subtree(1).accept(this, subtree);


            // expr5 → expr6
            // expr5 → [ type ] expr5
            // expr5 → new type
            // expr5 → del expr5
            // expr5 → unop expr5
            case Expr5:

                if (first instanceof DerLeaf) {
                    AbsType type;
                    AbsExpr expr;
                    AbsUnExpr.Oper operUn = AbsUnExpr.Oper.NOT;
                    switch (((DerLeaf) first).symb.token) {

                        case NEW:
                            type = (AbsType) node.subtree(1).accept(this, visArg);
                            return new AbsNewExpr(node.location(), type);

                        case DEL:
                            expr = (AbsExpr) node.subtree(1).accept(this, visArg);
                            return new AbsDelExpr(node.location(), expr);

                        case LBRACKET:
                            type = (AbsType) node.subtree(1).accept(this, visArg);
                            expr = (AbsExpr) node.subtree(3).accept(this, visArg);
                            return new AbsCastExpr(node.location(), type, expr);

                        case NOT:
                            operUn = AbsUnExpr.Oper.NOT;
                            break;
                        case ADD:
                            operUn = AbsUnExpr.Oper.ADD;
                            break;
                        case SUB:
                            operUn = AbsUnExpr.Oper.SUB;
                            break;
                        case MEM:
                            operUn = AbsUnExpr.Oper.MEM;
                            break;
                        case VAL:
                            operUn = AbsUnExpr.Oper.VAL;
                            break;
                    }

                    expr = (AbsExpr) node.subtree(1).accept(this, visArg);
                    return new AbsUnExpr(node.location(), operUn, expr);

                }
                else {
                    return first.accept(this, visArg);
                }


            // expr0 → xior expr1 expr0
            case Expr0:

            // expr10 -> and expr2 expr10
            case Expr10:

            // expr20 → cmp expr3 expr20
            case Expr20:

            // expr30 → plusminus expr4 expr30
            case Expr30:

            // expr40 → multdivmod expr5 expr40
            case Expr40:
                subtree = node.subtree(1).accept(this, null);
                location = new Location(visArg.location.getBegLine(), visArg.location.getBegColumn(), subtree.location().getEndLine(), subtree.location().getEndColumn());
                AbsBinExpr.Oper operBin = getBinOper(((DerLeaf)first).symb.token);
                AbsBinExpr binExpr = new AbsBinExpr(location, operBin, (AbsExpr)visArg, (AbsExpr)subtree);
                return node.subtree(2).accept(this, binExpr);


            // expr60 → [ expr ] expr60
            // expr60 → dot identifier expr60
            case Expr60:
                switch (((DerLeaf) first).symb.token) {
                    case LBRACKET:
                        subtree = node.subtree(1).accept(this, null);
                        location = new Location(visArg.location.getBegLine(), visArg.location.getBegColumn(), subtree.location().getEndLine(), subtree.location().getEndColumn());
                        AbsArrExpr arrExpr = new AbsArrExpr(location, (AbsExpr)visArg, (AbsExpr)subtree);
                        return node.subtree(3).accept(this, arrExpr);

                    case DOT:
                        name = ((DerLeaf) node.subtree(1)).symb.lexeme;
                        AbsVarName varName =  new AbsVarName(node.subtree(1).location(), name);
                        location = new Location(visArg.location.getBegLine(), visArg.location.getBegColumn(), varName.location().getEndLine(), varName.location().getEndColumn());
                        AbsRecExpr recExpr = new AbsRecExpr(location, (AbsExpr)visArg, varName);
                        return node.subtree(2).accept(this, recExpr);
                }


            // expr7 → ( expr )
            // expr7 → { stmtmulti : exprwhere }
            // expr7 → literal
            // expr7 → idenexprmulti
            case Expr7:
                if (first instanceof DerLeaf) {
                    AbsAtomExpr.Type type = AbsAtomExpr.Type.VOID;

                    switch(((DerLeaf) first).symb.token) {
                        case LPARENTHESIS:
                            return node.subtree(1).accept(this, null);

                        case LBRACE:
                            AbsStmts stmts = new AbsStmts(node.subtree(1).location(), new Vector<>());
                            stmts = (AbsStmts)node.subtree(1).accept(this, stmts);
                            AbsExprDecl exprDecl = (AbsExprDecl)node.subtree(3).accept(this, null);
                            return new AbsStmtExpr(node.location(), exprDecl.decls, stmts, exprDecl.expr);

                        case BOOLCONST:
                            type = AbsAtomExpr.Type.BOOL;
                            break;

                        case CHARCONST:
                            type = AbsAtomExpr.Type.CHAR;
                            break;

                        case INTCONST:
                            type = AbsAtomExpr.Type.INT;
                            break;

                        case PTRCONST:
                            type = AbsAtomExpr.Type.PTR;
                            break;

                        case VOIDCONST:
                            type = AbsAtomExpr.Type.VOID;
                            break;
                    }
                    return new AbsAtomExpr(node.location(), type, ((DerLeaf) first).symb.lexeme);

                }
                else {
                    return first.accept(this, null);
                }

            // exprwhere → expr exprwhere0
            case ExprWhere:
                // a bit different - added a custom class to support my nonterminal
                // the custom class should never appear in the output!

                AbsExpr expr =  (AbsExpr) first.accept(this, null);
                AbsDecls decls = new AbsDecls(node.location(), new Vector<>());
                decls = (AbsDecls) node.subtree(1).accept(this, decls);

                return new AbsExprDecl(node.location(), decls, expr);

            // exprwhere0 → where declmulti
            case ExprWhere0:
                AbsDecls decls1 = new AbsDecls(node.subtree(1).location(), new Vector<>());
                return node.subtree(1).accept(this, decls1);

            // exprassign → = expr
            case ExprAssign:
                return node.subtree(1).accept(this, null);

            // exprmulti → expr exprmulti0
            case ExprMulti:
                idx1 = 0;
                idx2 = 1;

            // same functionality in both
            // exprmulti0 → , expr exprmulti0
            case ExprMulti0:
                if (idx1 == 999) {
                    idx1 = 1;
                    idx2 = 2;
                }

                AbsExpr expr0 = (AbsExpr) node.subtree(idx1).accept(this, null);
                Vector<AbsExpr> vecExpr = ((AbsArgs) visArg).args();
                vecExpr.add(expr0);
                AbsArgs absArgs = new AbsArgs(visArg.location(), vecExpr);
                return node.subtree(idx2).accept(this, absArgs);

            // type → arr [ expr ] type
            // type → ptr type
            // type → identifier
            // type → void | bool | char | int
            // type → rec ( identypemulti )
            case Type:
                AbsAtomType.Type atomType = AbsAtomType.Type.VOID;

                switch (((DerLeaf)first).symb.token) {

                    case IDENTIFIER:
                        return new AbsTypeName(node.location(), ((DerLeaf) first).symb.lexeme);

                    case BOOL:
                        atomType = AbsAtomType.Type.BOOL;
                        break;

                    case VOID:
                        atomType = AbsAtomType.Type.VOID;
                        break;

                    case CHAR:
                        atomType = AbsAtomType.Type.CHAR;
                        break;

                    case INT:
                        atomType = AbsAtomType.Type.INT;
                        break;

                    case PTR:
                        AbsType subType = (AbsType) node.subtree(1).accept(this, null);
                        return new AbsPtrType(node.location(), subType);

                    case ARR:
                        AbsExpr len = (AbsExpr) node.subtree(2).accept(this, null);
                        AbsType elemType = (AbsType) node.subtree(4).accept(this, null);
                        return new AbsArrType(node.location(), len, elemType);

                    case REC:
                        AbsCompDecls compDecls = new AbsCompDecls(node.subtree(2).location(), new Vector<>());
                        compDecls = (AbsCompDecls) node.subtree(2).accept(this, compDecls);
                        return new AbsRecType(node.location(), compDecls);

                }
                return new AbsAtomType(node.location(), atomType);

            // stmt → expr stmt0
            // stmt → if expr then stmtmulti stmtelse end
            // stmt → while expr do stmtmulti end
            case Stmt:
                if (first instanceof DerNode) {
                    AbsExpr expr1 = (AbsExpr) first.accept(this, null);

                    // expr statement
                    if (((DerNode) node.subtree(1)).subtrees().size() == 0) {
                        return new AbsExprStmt(node.location(), expr1);
                    }
                    // assign statement
                    else
                        return node.subtree(1).accept(this, expr1);
                }
                else {
                    AbsStmts absStmts = new AbsStmts(node.subtree(3).location(), new Vector<>());
                    AbsExpr cond = (AbsExpr) node.subtree(1).accept(this, null);
                    AbsStmts thenBody = (AbsStmts) node.subtree(3).accept(this, absStmts);

                    switch (((DerLeaf) first).symb.token) {
                        case IF:
                            absStmts = new AbsStmts(node.subtree(4).location(), new Vector<>());
                            AbsStmts elseBody = (AbsStmts) node.subtree(4).accept(this, absStmts);
                            return new AbsIfStmt(node.location(), cond, thenBody, elseBody);

                        case WHILE:
                            return new AbsWhileStmt(node.location(), cond, thenBody);
                    }
                }


            // stmt0 → = expr
            case Stmt0:
                AbsExpr expr1 = (AbsExpr) node.subtree(1).accept(this ,null);
                location = new Location(visArg.location.getBegLine(), visArg.location.getBegColumn(), expr1.location().getEndLine(), expr1.location().getEndColumn());
                return new AbsAssignStmt(location, (AbsExpr) visArg, expr1);

            // stmtmulti → stmt stmtmulti0
            case StmtMulti:
                AbsStmt stmt = (AbsStmt) first.accept(this, null);
                Vector<AbsStmt> vecStmt = ((AbsStmts) visArg).stmts();
                vecStmt.add(stmt);
                AbsStmts stmts = new AbsStmts(visArg.location(), vecStmt);
                return node.subtree(1).accept(this, stmts);

            // stmtmulti0 → ; stmtmulti
            case StmtMulti0:

            // stmtelse → else stmtmulti
            case StmtElse:
                return node.subtree(1).accept(this, visArg);


            // decl → typ identifier : type
            // decl → var identifier : type
            // decl → fun identifier ( identypemulti ) : type exprassign
            case Decl:
                name = ((DerLeaf) node.subtree(1)).symb.lexeme;
                switch (((DerLeaf) first).symb.token) {
                    case TYP:
                        AbsType type = (AbsType) node.subtree(3).accept(this, null);
                        return new AbsTypeDecl(node.location(), name, type);

                    case VAR:
                        AbsType varType = (AbsType) node.subtree(3).accept(this, null);
                        return new AbsVarDecl(node.location(), name, varType);

                    case FUN:
                        AbsParDecls params = new AbsParDecls(node.subtree(3).location(), new Vector<>());
                        params = (AbsParDecls) node.subtree(3).accept(this, params);
                        AbsType returnType = (AbsType) node.subtree(6).accept(this, null);

                        if (((DerNode) node.subtree(7)).subtrees().size() == 0) {
                            return new AbsFunDecl(node.location(), name, params, returnType);
                        }
                        else {
                            AbsExpr value = (AbsExpr) node.subtree(7).accept(this, null);
                            return new AbsFunDef(node.location(), name, params, returnType, value);
                        }
                }


            // declmulti → decl declmulti0
            case DeclMulti:
                AbsDecl decl = (AbsDecl) first.accept(this, null);
                Vector<AbsDecl> vecDecl = ((AbsDecls) visArg).decls();
                vecDecl.add(decl);
                AbsDecls absDecls = new AbsDecls(visArg.location(), vecDecl);
                return node.subtree(1).accept(this, absDecls);

            // declmulti0 → ; declmulti
            case DeclMulti0:
                return node.subtree(1).accept(this, visArg);

            // identypemulti → identifier : type identypemulti0
            case IdenTypeMulti:

                name = ((DerLeaf) first).symb.lexeme;
                AbsType type = (AbsType) node.subtree(2).accept(this, null);
                Location location1 = new Location(first.location().getBegLine(), first.location().getBegColumn(), type.location().getEndLine(), type.location.getEndColumn());

                //visArg is a parameter denoting whether to use comp or parDecls
                if (visArg instanceof AbsParDecls) {
                    Vector<AbsParDecl> vec =((AbsParDecls) visArg).parDecls();
                    AbsParDecl parDecl = new AbsParDecl(location1, name, type);

                    vec.add(parDecl);
                    AbsParDecls parDecls = new AbsParDecls(visArg.location(), vec);

                    return node.subtree(3).accept(this, parDecls);
                }
                else {
                    Vector<AbsCompDecl> vec =((AbsCompDecls) visArg).compDecls();
                    AbsCompDecl compDecl = new AbsCompDecl(location1, name, type);

                    vec.add(compDecl);
                    AbsCompDecls compDecls = new AbsCompDecls(visArg.location(), vec);

                    return node.subtree(3).accept(this, compDecls);
                }


            // identypemulti0 → , identypemulti
            case IdenTypeMulti0:
                return node.subtree(1).accept(this, visArg);


            // idenexprmulti → identifier idenexprmulti0
            case IdenExprMulti:
                name = ((DerLeaf) first).symb.lexeme;

                // variable access
                if (((DerNode) node.subtree(1)).subtrees().size() == 0) {
                    return new AbsVarName(first.location(), name);
                }

                // function call
                else {
                    location = ((DerNode)node.subtree(1)).subtree(1).location();
                    AbsArgs args = new AbsArgs(location, new Vector<>());
                    args = (AbsArgs) node.subtree(1).accept(this, args);
                    return new AbsFunName(node.location(), name, args);
                }


            // idenexprmulti0 → ( exprmulti )
            case IdenExprMulti0:
                return node.subtree(1).accept(this, visArg);

            default:
                throw new Report.Error(node.location(), "Wrong node type " + node.label +" in visit(DerNode)");
        }

    }


    @Override
    public AbsTree visit(DerLeaf leaf, AbsTree visArg) {

        Report.warning(leaf.location(), leaf.symb.stringify() + " Visited a derivation leaf. This should not happen");
        return visArg;
    }

    private AbsBinExpr.Oper getBinOper (Term token) {
        switch(token) {
            case IOR:
                return AbsBinExpr.Oper.IOR;
            case XOR:
                return AbsBinExpr.Oper.XOR;
            case AND:
                return AbsBinExpr.Oper.AND;
            case EQU:
                return AbsBinExpr.Oper.EQU;
            case NEQ:
                return AbsBinExpr.Oper.NEQ;
            case LTH:
                return AbsBinExpr.Oper.LTH;
            case GTH:
                return AbsBinExpr.Oper.GTH;
            case LEQ:
                return AbsBinExpr.Oper.LEQ;
            case GEQ:
                return AbsBinExpr.Oper.GEQ;
            case ADD:
                return AbsBinExpr.Oper.ADD;
            case SUB:
                return AbsBinExpr.Oper.SUB;
            case MUL:
                return AbsBinExpr.Oper.MUL;
            case DIV:
                return AbsBinExpr.Oper.DIV;
            case MOD:
                return AbsBinExpr.Oper.MOD;
            default:
                throw new Report.Error("Token is not a binary operator");
        }
    }

}
