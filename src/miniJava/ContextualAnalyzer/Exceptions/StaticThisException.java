package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.ThisRef;

public class StaticThisException extends StaticReferenceException {
	private static final long serialVersionUID = -1181703353142929602L;

	public StaticThisException(ThisRef ref) {
		super("Cannot use this in a static context", ref.posn);
	}

}
