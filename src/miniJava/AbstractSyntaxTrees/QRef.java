/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class QRef extends QualifiedRef {
	public Reference ref;
	public Identifier id;
	
	public QRef(Reference ref, Identifier id) {
		this(ref, id, SourcePosition.ZERO);
	}
	
	public QRef(Reference ref, Identifier id, SourcePosition posn){
		super(posn);
		this.ref = ref;
		this.id  = id;
	}

	@Override
	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitQRef(this, o);
	}

	@Override
	public Identifier getIdent() {
		return this.id;
	}
	
	@Override
	public Reference getRef() {
		return this.ref;
	}	
	
	@Override
	public String toString() {
		return ref + "." + id;
	}
}
