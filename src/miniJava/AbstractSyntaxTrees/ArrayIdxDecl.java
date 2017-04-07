package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class ArrayIdxDecl extends Declaration {
	private Declaration arrDecl;
	
	public ArrayIdxDecl(Declaration arrDecl, ArrayType type, SourcePosition posn) {
		super("[...]", type.eltType, posn);
		this.arrDecl = arrDecl;
	}

	@Override
	public boolean allowStaticReference() {
		return false;
	}

	@Override
	public <A, R> R visit(Visitor<A, R> v, A o) {
		// TODO Auto-generated method stub
		return null;
	}

}
