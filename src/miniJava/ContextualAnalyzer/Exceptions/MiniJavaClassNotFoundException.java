package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.ClassType;

public class MiniJavaClassNotFoundException extends IdentificationException {
	private static final long serialVersionUID = 685302472196348778L;

	public MiniJavaClassNotFoundException(ClassType ct) {
		super("Class " + ct.className.spelling + " not found", ct.posn);
	}
	
}
