package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.ThisRef;

@SuppressWarnings("serial")
public class LefthandThisException extends IdentificationException {

	public LefthandThisException(ThisRef ref) {
		super("This cannot be used on the left hand of an assignment", ref.posn);
	}
	
}
