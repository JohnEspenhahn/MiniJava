/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class FieldDecl extends MemberDecl {
	private int ob_offset;

	public FieldDecl(boolean isPrivate, boolean isStatic, TypeDenoter t, String name, SourcePosition posn) {
		super(isPrivate, isStatic, t, name, posn);
		this.ob_offset = 0;
	}
	
	public FieldDecl(boolean isPrivate, boolean isStatic, TypeDenoter t, String name) {
		this(isPrivate, isStatic, t, name, SourcePosition.ZERO);
	}
	
	public FieldDecl(boolean isPrivate, boolean isStatic, boolean isReadonly, TypeDenoter t, String name) {
		this(isPrivate, isStatic, t, name, null);
		this.isReadonly = isReadonly;
	}

	public FieldDecl(MemberDecl md, SourcePosition posn) {
		super(md, posn);
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitFieldDecl(this, o);
	}
	
	public void setObjectOffset(int offset) {
		this.ob_offset = offset;
	}
	
	public int getObjectOffset() {
		return this.ob_offset;
	}
}
