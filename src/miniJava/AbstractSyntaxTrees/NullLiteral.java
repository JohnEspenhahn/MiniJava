package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.Token;

public class NullLiteral extends Terminal {

	public NullLiteral(Token t) {
		super(t);
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitNullLiteral(this, o);
	}

}
