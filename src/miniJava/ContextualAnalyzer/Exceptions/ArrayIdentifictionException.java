package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.IxIdRef;
import miniJava.AbstractSyntaxTrees.IxQRef;

public class ArrayIdentifictionException extends IdentificationException {
	private static final long serialVersionUID = 3995778397728710525L;

	public ArrayIdentifictionException(IxQRef ref) {
		super("Tried to index a non-array expression", ref.posn);
	}
	
	public ArrayIdentifictionException(IxIdRef ref) {
		super("Tried to index a non-array expression", ref.posn);
	}

}
