package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.Reference;

public class UndefinedReferenceException extends IdentificationException {
	private static final long serialVersionUID = -5843042839386562436L;

	public UndefinedReferenceException(Identifier ident) {
		super("The identifier " + ident.spelling + " is not declared in this context", ident.posn);
	}
	
	public UndefinedReferenceException(Reference ref) {
		super("The identifier " + ref + " is not declared in this context", ref.posn);
	}

}
