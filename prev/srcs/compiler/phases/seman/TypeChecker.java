package compiler.phases.seman;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.*;

/**
 * Tests whether expressions are well typed.
 * <p>
 * Methods of this visitor return the semantic type of a phrase being tested if
 * the AST node represents an expression or {@code null} otherwise. In the first
 * case methods leave their results in {@link SemAn#isOfType()}.
 *
 * @author sliva
 */
public class TypeChecker implements AbsVisitor<SemType, Object> {


    private TypeDefiner typeDefiner;

    private TypeTester typeTester;

    public TypeChecker() {
        this.typeDefiner = new TypeDefiner();
        this.typeTester = new TypeTester();
    }

    /**
     * expressions
     */

    // visArg is funDecl / funDef
    public SemType visit(AbsArgs node, Object visArg) {
        AbsFunDecl decl = (AbsFunDecl) visArg;

        // function call: check if the argument types match the parameter types
        for (int i = 0; i < node.args().size(); i++) {
            SemType argType = node.arg(i).accept(this, null);
            SemType parType = SemAn.descType().get(decl.parDecls.parDecl(i).type);

            if (!parType.matches(argType)) {
                throw new Report.Error(node.arg(i).location(), "Wrong argument type: expected " + parType + ", got " + argType);
            }
        }
        return null;
    }

    public SemType visit(AbsArrExpr node, Object visArg) {
        SemType indexType = node.index.accept(this, null);
        if (!indexType.isAKindOf(SemIntType.class)) {
            throw new Report.Error(node.index.location(), "Int required for array index, got " + indexType);
        }

        SemArrType arrType = (SemArrType) node.array.accept(this, null);
        SemAn.isOfType().put(node, arrType.elemType);
        return arrType.elemType;
    }

    public SemType visit(AbsAtomExpr node, Object visArg) {
        SemType type = new SemVoidType();

        switch (node.type) {
            case VOID:
                type = new SemVoidType();
                break;
            case BOOL:
                type = new SemBoolType();
                break;
            case CHAR:
                type = new SemCharType();
                break;
            case INT:
                type = new SemIntType();
                break;
            case PTR:
                type = new SemPtrType(new SemVoidType());
                break;
        }

        SemAn.isOfType().put(node, type);
        return type;
    }

    public SemType visit(AbsBinExpr node, Object visArg) {
        SemType type1 = node.fstExpr.accept(this, null);
        SemType type2 = node.sndExpr.accept(this, null);
        SemType type = new SemVoidType();

        switch (node.oper) {
            case IOR:
            case XOR:
            case AND:
                if (type1.isAKindOf(SemBoolType.class) && type2.isAKindOf(SemBoolType.class)) {
                    type = new SemBoolType();
                } else {
                    throw new Report.Error(node.location(), "Booleans required for operand " + node.oper + ", got " + type1 + " and " + type2);
                }
                break;

            case EQU:
            case NEQ:
            case LTH:
            case GTH:
            case LEQ:
            case GEQ:
                if (type1.matches(type2) && (type1.isAKindOf(SemBoolType.class) || type1.isAKindOf(SemIntType.class) ||
                        type1.isAKindOf(SemCharType.class) || type1.isAKindOf(SemPtrType.class))) {

                    type = new SemBoolType();
                } else {
                    throw new Report.Error(node.location(), "Matching types required for operand " + node.oper + ", got " + type1 + " and " + type2);
                }
                break;

            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                if (type1.isAKindOf(SemIntType.class) && type2.isAKindOf(SemIntType.class)) {
                    type = new SemIntType();
                } else {
                    throw new Report.Error(node.location(), "Ints required for operand " + node.oper + ", got " + type1 + " and " + type2);
                }
                break;
        }

        SemAn.isOfType().put(node, type);
        return type;
    }

    public SemType visit(AbsCastExpr node, Object visArg) {
        SemType castType = node.type.accept(this, null);
        SemType exprType = node.expr.accept(this, null);

        boolean isVoid = castType.isAKindOf(SemVoidType.class);
        boolean isPtrToVoid = castType.isAKindOf(SemPtrType.class) && exprType.isAKindOf(SemPtrType.class) &&
                ((SemPtrType) exprType).subType.isAKindOf(SemVoidType.class);
        boolean isToInt = castType.isAKindOf(SemIntType.class) && (exprType.isAKindOf(SemIntType.class) ||
                exprType.isAKindOf(SemBoolType.class) || exprType.isAKindOf(SemCharType.class));

        if (!(isVoid || isPtrToVoid || isToInt)) {
            throw new Report.Error(node.location(), "Wrong types for operand CAST, got " + exprType + " to " + castType);
        }

        SemAn.isOfType().put(node, castType);
        return castType;
    }

    public SemType visit(AbsDelExpr node, Object visArg) {
        SemType subType = node.expr.accept(this, null);
        SemType type;
        if (subType.isAKindOf(SemPtrType.class) && !((SemPtrType) subType).subType.isAKindOf(SemVoidType.class)) {
            type = new SemVoidType();
        } else {
            throw new Report.Error(node.location(), "Non-void pointer type required for operand DEL, got " + subType);
        }

        SemAn.isOfType().put(node, type);
        return type;
    }

    public SemType visit(AbsFunName node, Object visArg) {
        AbsDecl decl = SemAn.declAt().get(node);
        node.args.accept(this, decl);
        SemType type = SemAn.descType().get(decl.type);
        SemAn.isOfType().put(node, type);
        return type;
    }

    public SemType visit(AbsNewExpr node, Object visArg) {
        SemType subType = node.type.accept(this, null);
        SemType type;
        if (!subType.isAKindOf(SemVoidType.class)) {
            type = new SemPtrType(subType);
        } else {
            throw new Report.Error(node.location(), "Non-void type required for operand NEW, got " + subType);
        }

        SemAn.isOfType().put(node, type);
        return type;
    }

    // component access
    public SemType visit(AbsRecExpr node, Object visArg) {
        SemRecType recType = (SemRecType) node.record.accept(this, null);
        int idx = recType.compNames().indexOf(node.comp.name);

        if (idx == -1) {
            throw new Report.Error(node.location(), "Component " + node.comp.name + " does not exist in record");
        }

        SemType type = recType.compTypes().get(idx);
        SemAn.isOfType().put(node, type);
        return type;
    }

    public SemType visit(AbsUnExpr node, Object visArg) {
        SemType exprType = node.subExpr.accept(this, null);
        SemType type = new SemVoidType();

        switch (node.oper) {
            case NOT:
                if (exprType.isAKindOf(SemBoolType.class)) {
                    type = new SemBoolType();
                } else {
                    throw new Report.Error(node.location(), "Bool required for operand NOT, got " + exprType);
                }
                break;

            case ADD:
            case SUB:
                if (exprType.isAKindOf(SemIntType.class)) {
                    type = new SemIntType();
                } else {
                    throw new Report.Error(node.location(), "Int required for unary operand ADD or SUB, got " + exprType);
                }
                break;

            case MEM:
                if (!exprType.isAKindOf(SemVoidType.class) && SemAn.isLValue().get(node.subExpr)) {
                    type = new SemPtrType(exprType);
                } else {
                    throw new Report.Error(node.location(), "Non-void lvalue required for unary operand MEM, got " + exprType);
                }
                break;

            case VAL:
                if (exprType.isAKindOf(SemPtrType.class) && !((SemPtrType) exprType).subType.isAKindOf(SemVoidType.class)) {
                    type = ((SemPtrType) exprType).subType;
                } else {
                    throw new Report.Error(node.location(), "Pointer to non-void value required for unary operand VAL");
                }

                break;
        }

        SemAn.isOfType().put(node, type);
        return type;
    }

    public SemType visit(AbsVarName node, Object visArg) {
        AbsVarDecl decl = (AbsVarDecl) SemAn.declAt().get(node);
        SemType type = SemAn.descType().get(decl.type);
        SemAn.isOfType().put(node, type);
        return type;
    }


    /**
     * types
     */

    public SemType visit(AbsArrType node, Object visArg) {
        SemType type = node.accept(typeDefiner, this);
        node.accept(typeTester, this);
        if (type == null) {
            throw new Report.Error(node.location(), "Wrong array type");
        }
        return type;
    }

    public SemType visit(AbsAtomType node, Object visArg) {
        return node.accept(typeDefiner, this);
    }

    public SemType visit(AbsPtrType node, Object visArg) {
        SemType type = node.accept(typeDefiner, this);
        if (type == null) {
            throw new Report.Error(node.location(), "Wrong pointer type");
        }
        return type;
    }

    public SemType visit(AbsRecType node, Object visArg) {
        SemType type = node.accept(typeDefiner, this);
        if (type == null) {
            throw new Report.Error(node.location(), "Wrong record type");
        }
        return type;
    }

    public SemType visit(AbsTypeName node, Object visArg) {
        SemType type = node.accept(typeDefiner, this);
        if (type == null) {
            throw new Report.Error(node.location(), "Wrong type name");
        }
        return type;
    }


    /**
     * statements
     */

    public SemType visit(AbsAssignStmt node, Object visArg) {
        SemType type1 = node.dst.accept(this, null);
        SemType type2 = node.src.accept(this, null);

        if (! type1.matches(type2)) {
            throw new Report.Error(node.location(), "Required matching types in assign statement, got " + type1 + " and " + type2);
        }
        else if (! SemAn.isLValue().get(node.dst)) {
            throw new Report.Error(node.dst.location(), "Destination in assign statement must be an L-value");
        }

        return new SemVoidType();
    }

    public SemType visit(AbsExprStmt node, Object visArg) {
        SemType type = node.expr.accept(this, null);
        if (! type.isAKindOf(SemVoidType.class)) {
            throw new Report.Error(node.location(), "Required type void for statement expression, got " + type);
        }

        return new SemVoidType();
    }

    public SemType visit(AbsIfStmt node, Object visArg) {
        SemType condType = node.cond.accept(this, null);
        SemType thenType = node.thenBody.accept(this, null);
        SemType elseType = node.elseBody.accept(this, null);

        if (! condType.isAKindOf(SemBoolType.class)) {
            throw new Report.Error(node.cond.location(), "Required type bool for conditions");
        }
        else if (! (thenType.isAKindOf(SemVoidType.class) && elseType.isAKindOf(SemVoidType.class))) {
            throw new Report.Error(node.thenBody.location(), "Required type void for statements");
        }
        return thenType;
    }

    public SemType visit(AbsStmtExpr node, Object visArg) {
        node.decls.accept(typeDefiner, this);
        node.decls.accept(typeTester, this);

        SemType stmtType = node.stmts.accept(this, null);

        if (!stmtType.isAKindOf(SemVoidType.class)) {
            throw new Report.Error(node.stmts.location(), "Required type void for statements");
        }

        SemType exprType = node.expr.accept(this, null);
        SemAn.isOfType().put(node, exprType);
        return exprType;
    }

    public SemType visit(AbsStmts node, Object visArg) {
        for (AbsStmt stmt : node.stmts()) {
            SemType stmtType = stmt.accept(this, null);
            if (!stmtType.isAKindOf(SemVoidType.class)) {
                throw new Report.Error(stmt.location(), "Required type void for statements");
            }
        }
        return new SemVoidType();
    }

    public SemType visit(AbsWhileStmt node, Object visArg) {
        SemType condType = node.cond.accept(this, null);
        SemType bodyType = node.body.accept(this, null);

        if (! condType.isAKindOf(SemBoolType.class)) {
            throw new Report.Error(node.cond.location(), "Required type bool for conditions");
        }
        else if (! bodyType.isAKindOf(SemVoidType.class)) {
            throw new Report.Error(node.body.location(), "Required type void for statements");
        }
        return bodyType;
    }



}
