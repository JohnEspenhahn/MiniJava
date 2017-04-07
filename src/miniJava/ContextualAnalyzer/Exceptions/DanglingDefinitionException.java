package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Declaration;

@SuppressWarnings("serial")
public class DanglingDefinitionException extends IdentificationException {

	public DanglingDefinitionException(Declaration decl) {
		super("The declaration of " + decl.name + " is illegally dangling", decl.posn);
	}

}
