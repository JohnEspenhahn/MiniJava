package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.SyntacticAnalyzer.SourcePosition;

@SuppressWarnings("serial")
public class IdentificationException extends RuntimeException {
	
	public IdentificationException(Declaration decl) {
		this("Error with declaration " + decl, decl.posn);
	}
	
	public IdentificationException(String msg, SourcePosition posn) {
		super("*** " + msg + " at line " + posn.getLine());
	}

}
