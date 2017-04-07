/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeModifier.IndexQualifiedRuntimeModifier;
import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class IxIdRef extends BaseRef {
	
	public IxIdRef(Identifier id, Expression expr) {
		this(id, expr, SourcePosition.ZERO);
	}
	
	public IxIdRef(Identifier id, Expression expr, SourcePosition posn){
		super(posn);
		this.id = id;
		this.indexExpr = expr;
	}

	public Object visit(Visitor v, Object o) {
		return v.visitIxIdRef(this, o);
	}
	
	public Identifier id;
	public Expression indexExpr;

	@Override
	public RuntimeModifier getRuntimeModifier() {
		return new IndexQualifiedRuntimeModifier(
				getDecl().getRuntimeDesc().toBaseRuntimeModifier(), 
				indexExpr
			);
	}
}
