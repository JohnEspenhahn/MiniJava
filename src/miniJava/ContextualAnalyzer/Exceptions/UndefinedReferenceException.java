package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Reference;

public class UndefinedReferenceException extends IdentificationException {
	private static final long serialVersionUID = -5843042839386562436L;

	public UndefinedReferenceException(Reference ref) {
		super("The reference " + ref + " is not declared in this context", ref.posn);
	}

}
