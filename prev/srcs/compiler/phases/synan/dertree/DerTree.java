package compiler.phases.synan.dertree;

import common.report.Locatable;
import compiler.phases.synan.DerVisitor;

/**
 * Derivation tree.
 *
 * @author sliva
 */
public abstract class DerTree implements Locatable {

    /**
     * The method implementing the acceptor functionality.
     *
     * @param visitor The accepted visitor.
     * @param accArg  The acceptor's argument.
     * @return The acceptor's result.
     */
    public abstract <Result, Arg> Result accept(DerVisitor<Result, Arg> visitor, Arg accArg);

}
