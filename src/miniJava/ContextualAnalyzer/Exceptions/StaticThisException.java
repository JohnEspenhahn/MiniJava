package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.ThisRef;

@SuppressWarnings("serial")
public class StaticThisException extends StaticReferenceException {

	public StaticThisException(ThisRef ref) {
		super("Cannot use this in a static context", ref.posn);
	}

}
