package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.ThisRef;

public class LefthandThisException extends IdentificationException {
	private static final long serialVersionUID = -3280466404439058755L;

	public LefthandThisException(ThisRef ref) {
		super("This cannot be used on the left hand of an assignment", ref.posn);
	}
	
}
