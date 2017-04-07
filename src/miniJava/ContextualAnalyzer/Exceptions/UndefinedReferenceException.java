package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.Reference;

@SuppressWarnings("serial")
public class UndefinedReferenceException extends IdentificationException {

	public UndefinedReferenceException(Identifier ident) {
		super("The identifier " + ident.spelling + " is not declared in this context", ident.posn);
	}
	
	public UndefinedReferenceException(Reference ref) {
		super("The identifier " + ref + " is not declared in this context", ref.posn);
	}

}
