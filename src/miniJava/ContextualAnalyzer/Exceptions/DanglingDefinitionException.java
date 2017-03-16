package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Declaration;

public class DanglingDefinitionException extends IdentificationException {
	private static final long serialVersionUID = 1236201513495814930L;

	public DanglingDefinitionException(Declaration decl) {
		super("The declaration of " + decl.name + " is illegally dangling", decl.posn);
	}

}
