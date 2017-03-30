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

	// TODO

}
