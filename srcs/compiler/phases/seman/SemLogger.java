package compiler.phases.seman;

import common.logger.Logger;
import compiler.phases.abstr.AbsVisitor;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.SemType;

/**
 * The visitor that produces the log of the attributes computed during semantic
 * analysis.
 * <p>
 * This visitor does not traverses the abstract syntax tree. It is used as a
 * subvisitor of {@link compiler.phases.abstr#AbsLogger} and produces XML
 * representation of the computed attributes for each AST node separetely (when
 * called by a methods of {@link compiler.phases.abstr#AbsLogger} each time they
 * reach an AST node).
 *
 * @author sliva
 */
public class SemLogger implements AbsVisitor<Object, Object> {

    /**
     * The logger the log should be written to.
     */
    private final Logger logger;

    public SemLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Construct a new visitor with a logger the log should be written to.
     *
     * @param logger The logger the log should be written to.
     */
    @Override
    public Object visit(AbsArgs args, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsArrExpr arrExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(arrExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(arrExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsArrType arrType, Object visArg) {
        SemType type = SemAn.descType().get(arrType);
        if (type != null)
            type.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsAssignStmt assignStmt, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsAtomExpr atomExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(atomExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(atomExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsAtomType atomType, Object visArg) {
        SemType type = SemAn.descType().get(atomType);
        if (type != null)
            type.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsBinExpr binExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(binExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(binExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsCastExpr castExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(castExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(castExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsCompDecl compDecl, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsCompDecls compDecls, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsDecls decls, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsDelExpr delExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(delExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(delExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsExprStmt exprStmt, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsFunDecl funDecl, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsFunDef funDef, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsFunName funName, Object visArg) {
        AbsDecl decl = SemAn.declAt().get(funName);
        if (decl != null) {
            logger.begElement("declAt");
            logger.addAttribute("location", decl.location.toString());
            logger.endElement();
        }
        SemType type = SemAn.isOfType().get(funName);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(funName);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsIfStmt ifStmt, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsNewExpr newExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(newExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(newExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsParDecl parDecl, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsParDecls parDecls, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsPtrType ptrType, Object visArg) {
        SemType type = SemAn.descType().get(ptrType);
        if (type != null)
            type.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsRecExpr recExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(recExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(recExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsRecType recType, Object visArg) {
        SemType type = SemAn.descType().get(recType);
        if (type != null)
            type.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsStmtExpr stmtExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(stmtExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(stmtExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsStmts stmts, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsTypeDecl typeDecl, Object visArg) {
        SemType type = SemAn.declType().get(typeDecl);
        if (type != null)
            type.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsTypeName typeName, Object visArg) {
        AbsDecl decl = SemAn.declAt().get(typeName);
        if (decl != null) {
            logger.begElement("declAt");
            logger.addAttribute("location", decl.location.toString());
            logger.endElement();
        }
        SemType type = SemAn.descType().get(typeName);
        if (type != null)
            type.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsUnExpr unExpr, Object visArg) {
        SemType type = SemAn.isOfType().get(unExpr);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(unExpr);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsVarDecl varDecl, Object visArg) {
        return null;
    }

    @Override
    public Object visit(AbsVarName varName, Object visArg) {
        AbsVarDecl decl = (AbsVarDecl) SemAn.declAt().get(varName);
        if (decl != null) {
            logger.begElement("declAt");
            logger.addAttribute("location", decl.location.toString());
            logger.endElement();
        }
        SemType type = SemAn.isOfType().get(varName);
        if (type != null)
            type.log(logger);
        Boolean hasAddr = SemAn.isLValue().get(varName);
        if ((hasAddr != null) && (hasAddr == true)) {
            logger.begElement("lvalue");
            logger.endElement();
        }
        return null;
    }

    @Override
    public Object visit(AbsWhileStmt whileStmt, Object visArg) {
        return null;
    }

}
