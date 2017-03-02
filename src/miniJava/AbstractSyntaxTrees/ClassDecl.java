/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

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
	
	public Declaration getMember(String name) {
		for (FieldDecl f: fieldDeclList)
			if (f.name.equals(name))
				return f;
		
		for (MethodDecl m: methodDeclList)
			if (m.name.equals(name))
				return m;
		
		return null;
	}
}
