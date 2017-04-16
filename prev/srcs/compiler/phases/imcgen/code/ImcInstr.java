package compiler.phases.imcgen.code;

import compiler.phases.imcgen.*;

/**
 * Intermediate code instruction.
 * 
 * @author sliva
 *
 */
public abstract class ImcInstr {

	public abstract <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg);

}
