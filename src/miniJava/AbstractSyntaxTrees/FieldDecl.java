/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeDescription.AbsoluteAddress;
import miniJava.CodeGenerator.RuntimeDescription.ObjectRelativeAddress;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class FieldDecl extends MemberDecl {
	public FieldDecl(boolean isPrivate, boolean isStatic, TypeDenoter t, String name, SourcePosition posn) {
		super(isPrivate, isStatic, t, name, posn);
	}
	
	public FieldDecl(boolean isPrivate, boolean isStatic, TypeDenoter t, String name) {
		this(isPrivate, isStatic, false, t, name);
	}
	
	public FieldDecl(boolean isPrivate, boolean isStatic, boolean isReadonly, TypeDenoter t, String name) {
		this(isPrivate, isStatic, t, name, null);
		this.isReadonly = isReadonly;
	}

	public FieldDecl(MemberDecl md, SourcePosition posn) {
		super(md, posn);
	}

	public Object visit(Visitor v, Object o) {
		return v.visitFieldDecl(this, o);
	}
	
	/*
	 * CODE GENERATION	
	 */
	
	public void setObjectOffset(int offset) {
		this.setRuntimeDesc(new ObjectRelativeAddress(offset));
	}
	
	public void setAbsoluteAddress(int address) {
		this.setRuntimeDesc(new AbsoluteAddress(address));
	}
}
