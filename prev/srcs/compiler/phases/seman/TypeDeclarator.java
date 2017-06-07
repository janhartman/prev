package compiler.phases.seman;

import compiler.phases.abstr.AbsVisitor;
import compiler.phases.abstr.abstree.AbsTypeDecl;
import compiler.phases.seman.type.SemNamedType;

/**
 * Declares type synonyms introduced by type declarations.
 * <p>
 * Methods of this visitor return {@code null} but leave their results in
 * {@link SemAn#declType()}.
 *
 * @author sliva
 */
public class TypeDeclarator implements AbsVisitor<Object, Object> {

    /**
     * declarations
     */

    public Object visit(AbsTypeDecl node, Object visArg) {
        SemAn.declType().put(node, new SemNamedType(node));
        return null;
    }

}
