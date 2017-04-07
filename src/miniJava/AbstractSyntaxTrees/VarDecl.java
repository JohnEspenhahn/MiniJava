/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class VarDecl extends LocalDecl {
	
	public VarDecl(TypeDenoter t, String name) {
		this(t, name, SourcePosition.ZERO);
	}
	
	public VarDecl(TypeDenoter t, String name, SourcePosition posn) {
		super(name, t, posn);
	}
	
	public Object visit(Visitor v, Object o) {
		return v.visitVarDecl(this, o);
	}
	
}
