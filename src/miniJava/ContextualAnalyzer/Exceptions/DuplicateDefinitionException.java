package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Declaration;

public class DuplicateDefinitionException extends IdentificationException {
	private static final long serialVersionUID = -5119474592411760366L;

	public DuplicateDefinitionException(Declaration decl) {
		super("Duplicate definition of " + decl.name, decl.posn);
	}

}
