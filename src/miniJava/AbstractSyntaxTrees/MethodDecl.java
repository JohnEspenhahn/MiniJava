/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class MethodDecl extends MemberDecl {

	public ParameterDeclList parameterDeclList;
	public StatementList statementList;

	public MethodDecl(MemberDecl md, ParameterDeclList pl, StatementList sl) {
		this(md, pl, sl, SourcePosition.ZERO);
	}

	public MethodDecl(MemberDecl md, ParameterDeclList pl, StatementList sl, SourcePosition posn) {
		super(md, posn);
		parameterDeclList = pl;
		statementList = sl;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitMethodDecl(this, o);
	}
	
	@Override
	public MemberDecl getMember(Identifier ident) {
		return null; // Method's don't directly have members (must invoke first)
	}

}
