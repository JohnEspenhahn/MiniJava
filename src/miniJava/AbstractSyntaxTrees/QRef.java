/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeDescription.AbsoluteAddress;
import miniJava.CodeGenerator.RuntimeDescription.RelativeAddress;
import miniJava.CodeGenerator.RuntimeDescription.RuntimeDescription;
import miniJava.CodeGenerator.RuntimeModifier.FieldQualifiedRuntimeModifier;
import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;
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
	public Object visit(Visitor v, Object o) {
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
	
	@Override
	public RuntimeModifier getRuntimeModifier() {
		RuntimeDescription rd = getDecl().getRuntimeDesc();
		if (rd instanceof AbsoluteAddress) {
			// Can use absolute location
			return rd.toBaseRuntimeModifier();
		} else if (rd instanceof RelativeAddress) {
			// Make relative to another level
			return new FieldQualifiedRuntimeModifier(getRef().getRuntimeModifier(), (RelativeAddress) rd);
		} else {
			throw new RuntimeException("Unknown address type " + rd);
		}
	}
}
