/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;
import miniJava.CodeGenerator.RuntimeModifier.ThisRuntimeModifier;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class ThisRef extends BaseRef {
	
	public ThisRef() {
		this(SourcePosition.ZERO);
	}
	
	public ThisRef(SourcePosition posn) {
		super(posn);
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitThisRef(this, o);
	}
	
	@Override
	public String toString() {
		return "this";
	}

	@Override
	public RuntimeModifier getRuntimeModifier() {
		return new ThisRuntimeModifier();
	}
	
}
