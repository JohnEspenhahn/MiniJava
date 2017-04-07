package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.Reference;
import miniJava.SyntacticAnalyzer.SourcePosition;

@SuppressWarnings("serial")
public class StaticReferenceException extends IdentificationException {
	
	public StaticReferenceException(Identifier ident) {
		super("Cannot make a static reference to the non-static member " + ident.spelling, ident.posn);
	}
	
	public StaticReferenceException(Reference ref) {
		super("Cannot make a static reference to the non-static member " + ref, ref.posn);
	}
	
	public StaticReferenceException(Declaration decl, SourcePosition posn) {
		super("Cannot make a static reference to the non-static member " + decl.name, posn);
	}
	
	public StaticReferenceException(String s, SourcePosition p) {
		super(s, p);
	}
	
}
