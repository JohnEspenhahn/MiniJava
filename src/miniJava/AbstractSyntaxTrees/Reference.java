/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class Reference extends AST {
	private Declaration decl;
	
	// Error flags
	public boolean illegal_nonstatic_error;
	public boolean private_member_error;

	public Reference(SourcePosition posn) {
		super(posn);
		this.illegal_nonstatic_error = false;
		this.private_member_error = false;
	}

	public void setDecl(Declaration decl) {
		this.decl = decl;
	}
	
	public Declaration getDecl() {
		return this.decl;
	}
}
