package miniJava.CodeGenerator.RuntimeModifier;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Visitor;

public abstract class QualifiedRuntimeModifier implements RuntimeModifier {
	private RuntimeModifier base;
	private RuntimeOffsetGenerator offset;
	
	protected QualifiedRuntimeModifier(RuntimeModifier base, RuntimeOffsetGenerator offset) {
		this.base = base;
		this.offset = offset;
	}

	@Override
	public void store(Visitor<Object,Object> visitor, AST value) {
		base.load(visitor);
		offset.load(visitor);
		value.visit(visitor, null);
		emitStorePrim();
	}

	@Override
	public void load(Visitor<Object,Object> visitor) {
		base.load(visitor);
		offset.load(visitor);
		emitLoadPrim();
	}
	
	protected abstract void emitStorePrim();
	
	protected abstract void emitLoadPrim();

}
