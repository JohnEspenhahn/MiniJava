package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.IxIdRef;
import miniJava.AbstractSyntaxTrees.IxQRef;

@SuppressWarnings("serial")
public class ArrayIdentifictionException extends IdentificationException {

	public ArrayIdentifictionException(IxQRef ref) {
		super("Tried to index a non-array expression", ref.posn);
	}
	
	public ArrayIdentifictionException(IxIdRef ref) {
		super("Tried to index a non-array expression", ref.posn);
	}

}
