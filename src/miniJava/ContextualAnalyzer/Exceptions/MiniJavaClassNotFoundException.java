package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.ClassType;

@SuppressWarnings("serial")
public class MiniJavaClassNotFoundException extends IdentificationException {

	public MiniJavaClassNotFoundException(ClassType ct) {
		super("Class " + ct.className.spelling + " not found", ct.posn);
	}
	
}
