package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Reference;

public class ReadonlyAssignmentException extends IdentificationException {
	private static final long serialVersionUID = -4966318285282102347L;

	public ReadonlyAssignmentException(Reference ref) {
		super("Tried to assign a value to a readonly declaration of '" + ref.getDecl().name + "'", ref.posn);
	}

}
