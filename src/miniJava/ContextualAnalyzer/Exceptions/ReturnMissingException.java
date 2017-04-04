package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class ReturnMissingException extends IdentificationException {
	private static final long serialVersionUID = -6738737871382856946L;

	public ReturnMissingException(MethodDecl md, SourcePosition posn) {
		super("Missing ending return statement in " + md.name, posn);
	}

}
