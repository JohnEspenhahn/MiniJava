/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.ContextualAnalyzer.Exceptions.StaticReferenceException;
import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class ClassDecl extends Declaration {
	public int instanceSize;
	
	public FieldDeclList fieldDeclList;
	public MethodDeclList methodDeclList;
	
	public ClassDecl(String cn, FieldDeclList fdl, MethodDeclList mdl) {
		this(cn, fdl, mdl, SourcePosition.ZERO);
	}

	public ClassDecl(String cn, FieldDeclList fdl, MethodDeclList mdl, SourcePosition posn) {
		super(cn, new ClassType(new Identifier(new Token(TokenKind.IDENTIFIER, cn, null, null))), posn);
		fieldDeclList = fdl;
		methodDeclList = mdl;
	}

	public Object visit(Visitor v, Object o) {
		return v.visitClassDecl(this, o);
	}
	
	/*
	 * IDENTIFICATION
	 */
	
	@Override
	public boolean allowStaticReference() {
		return true;
	}
	
	@Override
	public MemberDecl getMember(Identifier ident) {
		// If calling directly on ClassDecl, trying to get member from class directly so require static
		return getMember(ident, true);
	}
	
	public MemberDecl getMember(Identifier ident, boolean requireStatic) {
		// Accessing class directly, so don't allow access to instance variables
		for (FieldDecl f: fieldDeclList)
			if (f.name.equals(ident.spelling)) {
				if (requireStatic && !f.allowStaticReference()) throw new StaticReferenceException(ident);
				else return f;
			}
		
		for (MethodDecl m: methodDeclList)
			if (m.name.equals(ident.spelling)) {
				if (requireStatic && !m.allowStaticReference()) throw new StaticReferenceException(ident);
				else return m;
			}
		
		return null;
	}
}
