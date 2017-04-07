package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.SyntacticAnalyzer.SourcePosition;

@SuppressWarnings("serial")
public class ReturnMissingException extends IdentificationException {

	public ReturnMissingException(MethodDecl md, SourcePosition posn) {
		super("Missing ending return statement in " + md.name, posn);
	}

}
