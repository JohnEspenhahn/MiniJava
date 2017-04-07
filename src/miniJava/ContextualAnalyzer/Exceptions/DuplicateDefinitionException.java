package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Declaration;

@SuppressWarnings("serial")
public class DuplicateDefinitionException extends IdentificationException {

	public DuplicateDefinitionException(Declaration decl) {
		super("Duplicate definition of " + decl.name, decl.posn);
	}

}
