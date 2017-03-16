/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class Declaration extends AST {
	
	public String name;
	public TypeDenoter type;
	
	public Declaration(String name, TypeDenoter type) {
		this(name, type, SourcePosition.ZERO);
	}
	
	public Declaration(String name, TypeDenoter type, SourcePosition posn) {
		super(posn);
		this.name = name;
		this.type = type;
		
		// Processing flags
		this.being_declared = false;
	}
	
	public MemberDecl getMember(Identifier ident) {
		return type.getMember(ident);
	}
	
	public abstract boolean allowStaticReference();
	
	// Processing flags
	public boolean being_declared;
}
