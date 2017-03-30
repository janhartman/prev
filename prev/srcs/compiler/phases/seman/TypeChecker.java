package compiler.phases.seman;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.*;

/**
 * Tests whether expressions are well typed.
 * 
 * Methods of this visitor return the semantic type of a phrase being tested if
 * the AST node represents an expression or {@code null} otherwise. In the first
 * case methods leave their results in {@link SemAn#isOfType()}.
 * 
 * @author sliva
 *
 */
public class TypeChecker implements AbsVisitor<SemType, Object> {

	// TODO

}
