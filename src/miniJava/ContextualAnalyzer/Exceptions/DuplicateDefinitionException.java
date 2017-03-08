package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Declaration;

public class DuplicateDefinitionException extends IdentificationException {
	private static final long serialVersionUID = -5119474592411760366L;

	public DuplicateDefinitionException(Declaration decl) {
		super("The variable with name " + decl.name + " was defined twice in this scope", decl.posn);
	}

}
