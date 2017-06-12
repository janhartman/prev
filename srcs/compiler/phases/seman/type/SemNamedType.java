package compiler.phases.seman.type;

import common.logger.Logger;
import compiler.phases.abstr.abstree.AbsDecl;
import compiler.phases.abstr.abstree.AbsTypeDecl;
import compiler.phases.seman.SemAn;

/**
 * Describes a type synonym.
 *
 * @author sliva
 */
public class SemNamedType extends SemType {

    /**
     * The AST declaration of this type synonym.
     */
    private final AbsTypeDecl typeDecl;

    /**
     * Constructs a new representation of a type synonym.
     *
     * @param typeDecl The AST declaration of this type synonym.
     */
    public SemNamedType(AbsTypeDecl typeDecl) {
        this.typeDecl = typeDecl;
    }

    /**
     * Returns the type this synonym is a synonym for.
     *
     * @return Returns the type this synonym is a synonym for.
     */
    public SemType type() {
        return SemAn.descType().get(typeDecl.type);
    }

    @Override
    public SemType actualType() {
        return type().actualType();
    }

    @Override
    public boolean assignable() {
        return type().assignable();
    }

    @Override
    public boolean sendable() {
        return type().sendable();
    }

    @Override
    public boolean recvable() {
        return type().recvable();
    }

    @Override
    public boolean matches(SemType that) {
        if (that instanceof SemNamedType) {
            return ((SemNamedType) that).matchesDecl(this.typeDecl);
        }

        return this.actualType().matches(that.actualType());
    }

    private boolean matchesDecl(AbsDecl declThat) {
        return this.typeDecl.equals(declThat);
    }

    @Override
    public void log(Logger logger) {
        if (logger == null)
            return;
        logger.begElement("type");
        logger.addAttribute("label", "NAME");
        logger.addAttribute("name", typeDecl.name);
        logger.addAttribute("loc", typeDecl.location.toString());
        logger.endElement();
    }

    @Override
    public String toString() {
        return typeDecl.name;
    }

    @Override
    public long size() {
        return type().size();
    }

}
