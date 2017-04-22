/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeDescription.RuntimeDescription;
import miniJava.ContextualAnalyzer.Exceptions.UndefinedReferenceException;
import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class Declaration extends AST {

	public String name;
	public TypeDenoter type;
	private RuntimeDescription rd;

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

	/*
	 * IDENTIFICATION
	 */

	public boolean being_declared;

	/**
	 * Get a member of this declaration's type
	 * @param ident The identifier of the member
	 * @return The member
	 * @throws UndefinedReferenceException If the type does not have a member matching the supplied identifier
	 */
	public MemberDecl getMember(Identifier ident) {
		MemberDecl decl = type.getMember(ident);
		if (decl == null)
			throw new UndefinedReferenceException(ident);
		return decl;
	}

	/**
	 * Utility function for checking if a declaration can be referenced from a static frame
	 * @return boolean
	 */
	public abstract boolean allowStaticReference();

	/*
	 * CODE GENERATION
	 */
	
	/**
	 * RuntimeDescription getter
	 * @return The runtime description
	 * @throws RuntimeException If used in the wrong place (before setRuntimeDesc is called)
	 */
	public RuntimeDescription getRuntimeDesc() {
		if (this.rd == null) throw new RuntimeException("Tried to get null runtime description!");
		return this.rd;
	}
	
	/**
	 * RuntimeDescription setter
	 * @param rd
	 */
	public void setRuntimeDesc(RuntimeDescription rd) {
		this.rd = rd;
	}
}
