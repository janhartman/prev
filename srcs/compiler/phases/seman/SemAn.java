package compiler.phases.seman;

import compiler.phases.Phase;
import compiler.phases.abstr.AbsAttribute;
import compiler.phases.abstr.AbsLogger;
import compiler.phases.abstr.Abstr;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.SemNamedType;
import compiler.phases.seman.type.SemType;

/**
 * Semantic analysis.
 *
 * @author sliva
 */
public class SemAn extends Phase {

    /**
     * The attribute that maps the usage of a name to its declaration.
     */
    private static final AbsAttribute<AbsName, AbsDecl> declAt = new AbsAttribute<AbsName, AbsDecl>();

    /**
     * The attribute that maps maps a type declaration to an internal
     * representation of a declared type.
     */
    private static final AbsAttribute<AbsTypeDecl, SemNamedType> declType = new AbsAttribute<AbsTypeDecl, SemNamedType>();

    /**
     * The attribute that maps a type expression to an internal representation
     * of a described type.
     */
    private static final AbsAttribute<AbsType, SemType> descType = new AbsAttribute<AbsType, SemType>();

    /**
     * The attribute that maps an expression to an internal representation of
     * its type.
     */
    private static final AbsAttribute<AbsExpr, SemType> isOfType = new AbsAttribute<AbsExpr, SemType>();

    /**
     * The attribute that maps a record to its symbol table.
     */
    private static final AbsAttribute<AbsRecType, SymbTable> recSymbTable = new AbsAttribute<AbsRecType, SymbTable>();

    /**
     * The attribute that tells whether an expression can evaluate to an lvalue.
     */
    private static final AbsAttribute<AbsExpr, Boolean> isLValue = new AbsAttribute<AbsExpr, Boolean>();

    /**
     * Returns an attribute that maps the usage of a name to its declaration.
     *
     * @return The attribute that maps the usage of a name to its declaration.
     */
    public static AbsAttribute<AbsName, AbsDecl> declAt() {
        return declAt;
    }

    /**
     * Returns an attribute that maps maps a type declaration to an internal
     * representation of a declared type.
     *
     * @return The attribute that maps maps a type declaration to an internal
     * representation of a declared type.
     */
    public static AbsAttribute<AbsTypeDecl, SemNamedType> declType() {
        return declType;
    }

    /**
     * Returns an attribute that maps a type expression to an internal
     * representation of a described type.
     *
     * @return The attribute that maps a type expression to an internal
     * representation of a described type.
     */
    public static AbsAttribute<AbsType, SemType> descType() {
        return descType;
    }

    /**
     * Returns an attribute that maps an expression to an internal
     * representation of its type.
     *
     * @return The attribute that maps an expression to an internal
     * representation of its type.
     */
    public static AbsAttribute<AbsExpr, SemType> isOfType() {
        return isOfType;
    }

    /**
     * Returns an attribute that maps a record to its symbol table.
     *
     * @return The attribute that maps a record to its symbol table.
     */
    public static AbsAttribute<AbsRecType, SymbTable> recSymbTable() {
        return recSymbTable;
    }

    /**
     * Returns an attribute that tells whether an expression can evaluate to an
     * lvalue.
     *
     * @return The attribute that tells whether an expression can evaluate to an
     * lvalue.
     */
    public static AbsAttribute<AbsExpr, Boolean> isLValue() {
        return isLValue;
    }

    /**
     * Constructs a new semantic analysis phase.
     */
    public SemAn() {
        super("seman");
    }

    @Override
    public void close() {
        declAt.lock();
        declType.lock();
        descType.lock();
        isOfType.lock();
        recSymbTable.lock();
        Abstr.absTree().accept(new AbsLogger(logger).addSubvisitor(new SemLogger(logger)), null);
        super.close();
    }

}
