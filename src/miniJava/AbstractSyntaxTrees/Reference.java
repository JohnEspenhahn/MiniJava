/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.ContextualAnalyzer.Exceptions.UndefinedReferenceException;
import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class Reference extends AST {
	private Declaration decl;

	public Reference(SourcePosition posn) {
		super(posn);
	}

	public void setDecl(Declaration decl) {
		if (decl == null) throw new UndefinedReferenceException(this);		
		this.decl = decl;
	}
	
	public Declaration getDecl() {
		return this.decl;
	}
}
