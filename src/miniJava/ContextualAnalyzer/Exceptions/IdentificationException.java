package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class IdentificationException extends RuntimeException {
	private static final long serialVersionUID = 5177887660410396558L;
	
	public IdentificationException(Declaration decl) {
		this("Error with declaration " + decl, decl.posn);
	}
	
	public IdentificationException(String msg, SourcePosition posn) {
		super("*** " + msg + " at line " + posn.getLine());
	}

}
