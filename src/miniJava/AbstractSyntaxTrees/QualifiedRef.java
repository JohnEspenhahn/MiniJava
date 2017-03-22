package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class QualifiedRef extends Reference {

	public QualifiedRef(SourcePosition posn){
		super(posn);
	}

	public abstract Identifier getIdent();
	public abstract Reference getRef();
}
