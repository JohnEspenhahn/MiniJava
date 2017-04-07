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
	public RuntimeDescription rd;

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

	public MemberDecl getMember(Identifier ident) {
		MemberDecl decl = type.getMember(ident);
		if (decl == null)
			throw new UndefinedReferenceException(ident);
		return decl;
	}

	public abstract boolean allowStaticReference();

	/*
	 * CODE GENERATION
	 */
	
}
