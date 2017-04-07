/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class Package extends AST {
	public ClassDeclList classDeclList;
	public MethodDecl main;

	public Package(ClassDeclList cdl) {
		this(cdl, SourcePosition.ZERO);
	}

	public Package(ClassDeclList cdl, SourcePosition posn) {
		super(posn);
		classDeclList = cdl;
	}

	public Object visit(Visitor v, Object o) {
		return v.visitPackage(this, o);
	}
}
