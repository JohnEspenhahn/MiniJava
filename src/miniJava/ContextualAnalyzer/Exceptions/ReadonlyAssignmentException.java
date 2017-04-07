package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Reference;

@SuppressWarnings("serial")
public class ReadonlyAssignmentException extends IdentificationException {

	public ReadonlyAssignmentException(Reference ref) {
		super("Tried to assign a value to a readonly declaration of '" + ref.getDecl().name + "'", ref.posn);
	}

}
