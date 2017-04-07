package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeDescription.AbsoluteAddress;
import miniJava.CodeGenerator.RuntimeDescription.RelativeAddress;
import miniJava.CodeGenerator.RuntimeDescription.RuntimeDescription;
import miniJava.CodeGenerator.RuntimeModifier.FieldQualifiedRuntimeModifier;
import miniJava.CodeGenerator.RuntimeModifier.IndexQualifiedRuntimeModifier;
import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;
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
	public Object visit(Visitor v, Object o) {
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

	@Override
	public RuntimeModifier getRuntimeModifier() {
		RuntimeModifier arrRuntimeModifier = null;
		RuntimeDescription rd = getDecl().getRuntimeDesc();
		if (rd instanceof AbsoluteAddress) {
			// Can use absolute location
			arrRuntimeModifier = rd.toBaseRuntimeModifier();
		} else if (rd instanceof RelativeAddress) {
			// Make relative to another level
			arrRuntimeModifier = 
					new FieldQualifiedRuntimeModifier(getRef().getRuntimeModifier(), (RelativeAddress) rd);
		} else {
			throw new RuntimeException("Unknown address type " + rd);
		}
		
		return new IndexQualifiedRuntimeModifier(arrRuntimeModifier, ixExpr);
	}
}
