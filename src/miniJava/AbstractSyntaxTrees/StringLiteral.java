/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.Token;

public class StringLiteral extends Terminal {
	public StringLiteralDecl decl;

	public StringLiteral(Token t) {
		super(t);
	}

	public Object visit(Visitor v, Object o) {
		return v.visitStringLiteral(this, o);
	}
}