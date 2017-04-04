package compiler.phases.seman;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.*;

import java.util.HashMap;
import java.util.Vector;

/**
 * Tests whether types constructed by {@link TypeDefiner} make sense.
 * 
 * @author sliva
 *
 */
public class TypeTester implements AbsVisitor<Object, Object> {


    /**
     * types
     */

    public Object visit(AbsArrType node, Object visArg) {
        node.elemType.accept(this, null);
        SemType arrType = SemAn.descType().get(node);

        if (arrType == null || ! arrType.isAKindOf(SemArrType.class)) {
            throw new Report.Error(node.location(), "Semantic array type not found");
        }
        return null;
    }

    public Object visit(AbsAtomType node, Object visArg) {
        SemType atomType = SemAn.descType().get(node);

        if (atomType == null) {
            throw new Report.Error(node.location(), "Semantic atom type not found");
        }
        return null;
    }

    public Object visit(AbsPtrType node, Object visArg) {
        node.subType.accept(this, null);
        SemType ptrType = SemAn.descType().get(node);

        if (ptrType == null || ! ptrType.isAKindOf(SemPtrType.class)) {
            throw new Report.Error(node.location(), "Semantic pointer type not found");
        }
        return null;
    }

    public Object visit(AbsRecType node, Object visArg) {
        node.compDecls.accept(this, null);
        SemType recType = SemAn.descType().get(node);

        if (recType == null || ! recType.isAKindOf(SemRecType.class)) {
            throw new Report.Error(node.location(), "Semantic record type not found");
        }
        return null;
    }

    public Object visit(AbsTypeName node, Object visArg) {
        SemType namedType = SemAn.descType().get(node);

        if (namedType == null)  {
            throw new Report.Error(node.location(), "Semantic named type not found");
        }
        else if (!(namedType instanceof SemNamedType)) {
            throw new Report.Error(node.location(), "Type is not a semantic named type, got " + namedType);
        }
        return null;
    }


    /**
     * declarations
     */

    public Object visit(AbsDecls node, Object visArg) {
        checkTypeHierarchy(node);

        for (AbsDecl decl : node.decls()) {
            decl.accept(this, visArg);
        }

        return null;
    }

    public Object visit(AbsTypeDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public Object visit(AbsCompDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public Object visit(AbsCompDecls node, Object visArg) {
        for (AbsCompDecl compDecl : node.compDecls()) {
            compDecl.accept(this, null);
        }
        return null;
    }

    public Object visit(AbsFunDecl node, Object visArg) {
        node.type.accept(this, null);
        node.parDecls.accept(this, null);
        return null;
    }

    public Object visit(AbsFunDef node, Object visArg) {
        node.type.accept(this, null);
        node.parDecls.accept(this, null);
        node.value.accept((TypeChecker) visArg, null);

        return null;
    }

    public Object visit(AbsParDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public Object visit(AbsParDecls node, Object visArg) {
        for (AbsParDecl parDecl : node.parDecls()) {
            parDecl.accept(this, null);
        }
        return null;
    }

    public Object visit(AbsVarDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    /**
     * checks the type declaration hierarchy for recursive relationships
     */
    private void checkTypeHierarchy(AbsDecls node) {
        Vector <AbsDecl> decls = node.decls();

        // the hashmap represents relations
        HashMap<String, String> typeRelations = new HashMap<>();
        for (AbsDecl decl : decls) {
            if (decl instanceof AbsTypeDecl) {
                for (AbsDecl decl2 : decls) {
                    if (decl.equals(decl2))
                        continue;

                    addToHierarchy(decl, decl2, typeRelations);
                }
            }
        }

        // a "flattened" hierarchy - a value must not appear more than once in the chainif the hierarchy is not recursive
        Vector<String> hierarchy = new Vector<>();
        for (String key : typeRelations.keySet()) {
            hierarchy.add(key);
            String value = typeRelations.get(key);

            if (! hierarchy.contains(value)) {
                hierarchy.add(value);
            }
            else {
                throw new Report.Error(node.location(), "Recursive type hierarchy found");
            }
        }
    }

    private void addToHierarchy(AbsDecl srcDecl, AbsDecl dstDecl, HashMap<String, String> typeRelations) {

        if (srcDecl.type instanceof AbsTypeName) {
            if (dstDecl.name.equals(((AbsTypeName) srcDecl.type).name)) {
                typeRelations.put(srcDecl.name, dstDecl.name);
            }
        }

        else if (srcDecl.type instanceof AbsRecType) {
            AbsRecType recType = (AbsRecType) srcDecl.type;
            for (AbsCompDecl compDecl : recType.compDecls.compDecls()) {

                if (compDecl.type.getClass() == dstDecl.type.getClass()) {
                    typeRelations.put(srcDecl.name, dstDecl.name);
                }

                // if record / array, go deeper
                else if (compDecl.type instanceof AbsArrType || compDecl.type instanceof AbsRecType){
                    addToHierarchy(compDecl, dstDecl, typeRelations);
                }
            }
        }

        else if (srcDecl.type instanceof AbsArrType) {
            AbsArrType arrType = (AbsArrType) srcDecl.type;
            if (arrType.elemType.getClass() == dstDecl.type.getClass()) {
                typeRelations.put(srcDecl.name, dstDecl.name);
            }
            // TODO if nested record / array ?

        }

    }

}
