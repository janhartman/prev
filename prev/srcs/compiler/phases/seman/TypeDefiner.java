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
        SemType elemType = node.elemType.accept(this, visArg);

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
        SemType subType = node.subType.accept(this, visArg);

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
            SemType type = compDecl.accept(this, visArg);
            if (type == null) {
                return null;
            }
            comps.add(type);
            names.add(compDecl.name);

            try {
                symbTable.ins(compDecl.name, compDecl);
            }
            catch (SymbTable.CannotInsNameException cine) {
                throw new Report.Error(compDecl.location(), "Component with name " + compDecl.name + " already exists in record");
            }
        }

        SemRecType recType = new SemRecType(names, comps);
        SemAn.descType().put(node, recType);
        SemAn.recSymbTable().put(node, symbTable);
        return recType;
    }

    @SuppressWarnings("unchecked")
    public SemType visit(AbsTypeName node, Object visArg) {
        AbsTypeDecl typeDecl = (AbsTypeDecl) SemAn.declAt().get(node);
        SemNamedType semNamedType = SemAn.declType().get(typeDecl);
        SemType type;

        if (semNamedType == null) {
            throw new Report.Error(node.location(), "Named type not declared");
        }

        /*
         * Resolves any possible recursive types by sending a HashMap containing String - AbsTypeName pairs.
         * If the current type name is already in the hashmap and it does not resolve to a type which
         * contains a pointer to itself, the hierarchy is recursive.
         */
        HashMap <String, AbsTypeName> hierarchy;

        if (visArg != null && visArg instanceof HashMap) {
            hierarchy = (HashMap<String, AbsTypeName>) visArg;
        }
        else {
            hierarchy = new HashMap<>();
        }

        if (hierarchy.get(node.name) != null) {
            for (String s : hierarchy.keySet()) {
                AbsTypeName typeName = hierarchy.get(s);
                AbsTypeDecl decl = (AbsTypeDecl) SemAn.declAt().get(typeName);

                if (decl.type instanceof AbsPtrType) {
                    AbsPtrType ptrType = (AbsPtrType) decl.type;
                    if (ptrType.subType instanceof AbsTypeName) {
                        type = semNamedType;
                        SemAn.descType().put(node, type);
                        return type;
                    }
                }

                if (decl.type instanceof AbsRecType) {
                    AbsRecType recType = (AbsRecType) decl.type;
                    for (AbsCompDecl compDecl : recType.compDecls.compDecls()) {
                        if (compDecl.type instanceof AbsPtrType) {
                            AbsType subType = ((AbsPtrType) compDecl.type).subType;
                            if (subType instanceof AbsTypeName) {
                                type = semNamedType;
                                SemAn.descType().put(node, type);
                                return type;
                            }
                        }
                    }
                }

                if (decl.type instanceof AbsArrType) {
                    AbsArrType arrType = (AbsArrType) decl.type;
                    if (arrType.elemType instanceof AbsPtrType) {
                        AbsType subType = ((AbsPtrType) arrType.elemType).subType;
                        if (subType instanceof AbsTypeName) {
                            type = semNamedType;
                            SemAn.descType().put(node, type);
                            return type;
                        }
                    }
                }


            }

            throw new Report.Error(node.location(), "Recursive type hierarchy found");
        }
        else {
            hierarchy.put(node.name, node);
            type = typeDecl.type.accept(this, hierarchy);
        }

        SemAn.descType().put(node, type);
        return type;
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
            if (decl instanceof AbsVarDecl) {
                decl.accept(this, null);
            }
        }

        for (AbsDecl decl : node.decls()) {
            if (decl instanceof AbsFunDecl) {
                ((AbsFunDecl) decl).type.accept(this, null);
                ((AbsFunDecl) decl).parDecls.accept(this, null);
            }
        }

        for (AbsDecl decl : node.decls()) {
            if (decl instanceof AbsFunDef) {
                decl.accept(this, visArg);
            }
        }
        return null;
    }

    public SemType visit(AbsTypeDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public SemType visit(AbsCompDecl node, Object visArg) {
        return node.type.accept(this, visArg);
    }

    public SemType visit(AbsCompDecls node, Object visArg) {
        for (AbsCompDecl compDecl : node.compDecls()) {
            compDecl.accept(this, null);
        }
        return null;
    }

    public SemType visit(AbsFunDecl node, Object visArg) {
        node.type.accept(this, null);
        return null;
    }

    public SemType visit(AbsFunDef node, Object visArg) {
        SemType returnType = node.type.accept(this, null);
        SemType valueType = node.value.accept((TypeChecker) visArg, null);

        if (! returnType.matches(valueType)) {
            throw new Report.Error(node.location(), "Required matching types for declared returned type and actual returned type, got " + returnType + " and " + valueType);
        }
        return null;
    }

    public SemType visit(AbsParDecl node, Object visArg) {
        SemType parType = node.type.accept(this, null);
        if (! (parType.isAKindOf(SemBoolType.class) || parType.isAKindOf(SemIntType.class)
            || parType.isAKindOf(SemCharType.class) || parType.isAKindOf(SemPtrType.class)
            || parType.isAKindOf(SemVoidType.class))) {
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
