/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class IdRef extends BaseRef {
	public Identifier id;
	
	public IdRef(Identifier id) {
		this(id, SourcePosition.ZERO);
	}
	
	public IdRef(Identifier id, SourcePosition posn){
		super(posn);
		this.id = id;
	}
		
	public <A,R> R visit(Visitor<A,R> v, A o) {
		return v.visitIdRef(this, o);
	}
	
	@Override
	public String toString() {
		return id.toString();
	}

	@Override
	public RuntimeModifier getRuntimeModifier() {
		return getDecl().getRuntimeDesc().toBaseRuntimeModifier();
	}
}
