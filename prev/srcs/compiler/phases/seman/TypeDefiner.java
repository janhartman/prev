package compiler.phases.seman;

import java.util.*;
import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.*;

/**
 * Constructs semantic representation of each type.
 * 
 * Methods of this visitor return the constructed semantic type if the AST node
 * represents a type or {@code null} otherwise. In either case methods leave
 * their results in {@link SemAn#descType()}.
 * 
 * @author sliva
 *
 */
public class TypeDefiner implements AbsVisitor<SemType, Object> {

    /**
     * types
     */

    public SemType visit(AbsArrType node, Object visArg) {
        Long len = node.len.accept(new ConstIntEvaluator(), null);
        SemType elemType = node.elemType.accept(this, null);

        if (len == null) {
            throw new Report.Error(node.len.location(), "Array length must be a constant integet expression");
        }

        if (elemType == null) {
            return null;
        }
        else {
            SemArrType arrType = new SemArrType(len, elemType);
            SemAn.descType().put(node, arrType);
            return arrType;
        }
    }

    public SemType visit(AbsAtomType node, Object visArg) {
        SemType atomType;

        switch(node.type) {
            case BOOL:
                atomType = new SemBoolType();
                break;

            case CHAR:
                atomType = new SemCharType();
                break;

            case INT:
                atomType = new SemIntType();
                break;

            case VOID:
                atomType = new SemVoidType();
                break;

            default:
                atomType = null;
        }
        SemAn.descType().put(node, atomType);
        return atomType;
    }

    public SemType visit(AbsPtrType node, Object visArg) {
        SemType subType = node.subType.accept(this, null);

        if (subType == null) {
            return null;
        }
        else {
            SemPtrType ptrType = new SemPtrType(subType);
            SemAn.descType().put(node, ptrType);
            return ptrType;
        }
    }

    public SemType visit(AbsRecType node, Object visArg) {
        Vector<SemType> comps = new Vector<>();
        Vector<String> names = new Vector<>();

        SymbTable symbTable = new SymbTable();
        symbTable.newScope();

        for (AbsCompDecl compDecl : node.compDecls.compDecls()) {
            SemType type = compDecl.accept(this, null);
            if (type == null) {
                return null;
            }
            comps.add(type);
            names.add(compDecl.name);

            try {
                symbTable.ins(compDecl.name, compDecl);
            }
            catch (SymbTable.CannotInsNameException cine) {
                throw new Report.Error(compDecl.location(), "Component with name " + compDecl.name + " exists");
            }
        }

        SemRecType recType = new SemRecType(names, comps);
        SemAn.descType().put(node, recType);
        SemAn.recSymbTable().put(node, symbTable);
        return recType;
    }

    public SemType visit(AbsTypeName node, Object visArg) {
        AbsTypeDecl typeDecl = (AbsTypeDecl) SemAn.declAt().get(node);
        SemNamedType semNamedType = SemAn.declType().get(typeDecl);
        if (semNamedType == null) {
            throw new Report.Error(node.location(), "Named type not declared");
        }
        SemAn.descType().put(node, semNamedType);
        return semNamedType;
    }


    /**
     * declarations
     */

    public SemType visit(AbsDecls node, Object visArg) {
        for (AbsDecl decl : node.decls()) {
            if (decl instanceof AbsTypeDecl) {
                decl.accept(new TypeDeclarator(), this);
            }
        }

        for (AbsDecl decl : node.decls()) {
            decl.accept(this, visArg);
        }
        return null;
    }

    public SemType visit(AbsTypeDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public SemType visit(AbsCompDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public SemType visit(AbsCompDecls node, Object visArg) {
        for (AbsCompDecl compDecl : node.compDecls()) {
            compDecl.accept(this, null);
        }
        return null;
    }

    public SemType visit(AbsFunDecl node, Object visArg) {
        node.type.accept(this, null);
        node.parDecls.accept(this, null);
        return null;
    }

    public SemType visit(AbsFunDef node, Object visArg) {
        SemType returnType = node.type.accept(this, null);
        node.parDecls.accept(this, null);
        SemType valueType = node.value.accept((TypeChecker) visArg, null);

        if (! returnType.matches(valueType)) {
            throw new Report.Error(node.location(), "Required matching types for declared returned type and actual returned type, got " + returnType + " and " + valueType);
        }
        return null;
    }

    public SemType visit(AbsParDecl node, Object visArg) {
        SemType parType = node.type.accept(this, null);
        if (! (parType.isAKindOf(SemBoolType.class) || parType.isAKindOf(SemIntType.class)
            || parType.isAKindOf(SemCharType.class) || parType.isAKindOf(SemPtrType.class))) {
            throw new Report.Error(node.location(), "Parameter type " + parType + " not allowed");
        }

        return parType;
    }

    public SemType visit(AbsParDecls node, Object visArg) {
        for (AbsParDecl parDecl : node.parDecls()) {
            parDecl.accept(this, null);
        }
        return null;
    }

    public SemType visit(AbsVarDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

}
