package compiler.phases.seman;

import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.*;

/**
 * Declares type synonyms introduced by type declarations.
 * 
 * Methods of this visitor return {@code null} but leave their results in
 * {@link SemAn#declType()}.
 * 
 * @author sliva
 *
 */
public class TypeDeclarator implements AbsVisitor<Object, Object> {

	// TODO
	
}
