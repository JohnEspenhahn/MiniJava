/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.ContextualAnalyzer.Exceptions.StaticReferenceException;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class ClassDecl extends Declaration {

	public FieldDeclList fieldDeclList;
	public MethodDeclList methodDeclList;
	
	public ClassDecl(String cn, FieldDeclList fdl, MethodDeclList mdl) {
		this(cn, fdl, mdl, SourcePosition.ZERO);
	}

	public ClassDecl(String cn, FieldDeclList fdl, MethodDeclList mdl, SourcePosition posn) {
		super(cn, null, posn);
		fieldDeclList = fdl;
		methodDeclList = mdl;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitClassDecl(this, o);
	}
	
	@Override
	public boolean allowStaticReference() {
		return true;
	}
	
	@Override
	public MemberDecl getMember(Identifier ident) {
		// Accessing class directly, so don't allow access to instance variables
		for (FieldDecl f: fieldDeclList)
			if (f.name.equals(ident.spelling)) {
				if (!f.allowStaticReference()) throw new StaticReferenceException(ident);
				else return f;
			}
		
		for (MethodDecl m: methodDeclList)
			if (m.name.equals(ident.spelling)) {
				if (!m.allowStaticReference()) throw new StaticReferenceException(ident);
				else return m;
			}
		
		return null;
	}
}
