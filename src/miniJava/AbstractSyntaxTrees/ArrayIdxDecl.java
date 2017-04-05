package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class ArrayIdxDecl extends Declaration {

	public ArrayIdxDecl(ArrayType type, SourcePosition posn) {
		super("[...]", type.eltType, posn);
		// TODO Auto-generated constructor stub
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
