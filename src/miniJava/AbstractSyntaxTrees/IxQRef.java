package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class IxQRef extends QualifiedRef {
	public Reference ref;
	public Identifier id;
	public Expression ixExpr;
	
	public IxQRef(Reference ref, Identifier id, Expression exp) {
		this(ref, id, exp, SourcePosition.ZERO);
	}
	
	public IxQRef(Reference ref, Identifier id, Expression exp, SourcePosition posn){
		super(posn);
		this.ref = ref;
		this.id  = id;
		this.ixExpr = exp;
	}

	@Override
	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitIxQRef(this, o);
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
		return ref + "." + id + "[...]";
	}
}
