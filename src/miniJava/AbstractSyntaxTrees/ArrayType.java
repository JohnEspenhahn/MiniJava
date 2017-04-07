/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */

package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class ArrayType extends TypeDenoter {
	public static MemberDecl LENGTH;
	
	static {
		LENGTH = new FieldDecl(false, false, true, new BaseType(TypeKind.INT), "length");
	}
	
	public TypeDenoter eltType;
	
	public ArrayType(TypeDenoter eltType) {
		this(eltType, SourcePosition.ZERO);
	}

	public ArrayType(TypeDenoter eltType, SourcePosition posn) {
		super(TypeKind.ARRAY, posn);
		this.eltType = eltType;
	}

	public Object visit(Visitor v, Object o) {
		return v.visitArrayType(this, o);
	}

	@Override
	public MemberDecl getMember(Identifier ident) {
		if (ident.spelling.equals("length"))
			return LENGTH;
		else 
			return null;
	}
}
