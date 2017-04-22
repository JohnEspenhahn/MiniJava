package miniJava.ContextualAnalyzer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import miniJava.AbstractSyntaxTrees.BlockStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.StringLiteral;
import miniJava.AbstractSyntaxTrees.StringLiteralDecl;
import miniJava.CodeGenerator.GlobalClasses;
import miniJava.ContextualAnalyzer.Exceptions.DuplicateDefinitionException;
import miniJava.ContextualAnalyzer.Exceptions.StaticReferenceException;
import miniJava.ContextualAnalyzer.Exceptions.UndefinedReferenceException;

public class ScopeStack {
	// Stack of all current scopes
	private LinkedList<Scope> scopes;
	private Map<String, StringLiteralDecl> stringLits;

	public ScopeStack() {
		// Use linked list to get correct iteration order
		this.scopes = new LinkedList<Scope>();
		this.stringLits = new HashMap<String, StringLiteralDecl>();

		createLevel0();
	}

	private void createLevel0() {
		// Force hide-able package layer
		this.scopes.push(new Scope(null, true, Scope.Kind.PREDEFINED));

		GlobalClasses.define(this);

		// Force main package layer
		this.scopes.push(new Scope(null, true, Scope.Kind.PACKAGE));
	}

	/**
	 * Open this ClassDecl's scope, creating it if need be
	 * @param decl
	 */
	public void openScope(ClassDecl decl) {
		Scope scope = decl.scope;
		if (scope == null) {
			scope = new Scope(decl, true, Scope.Kind.CLASS);
			decl.scope = scope;
		}
		this.scopes.push(scope);
	}

	public void openScope(MethodDecl decl) {
		this.scopes.push(new Scope(decl, decl.isStatic, Scope.Kind.METHOD));
	}

	public void openScope(BlockStmt decl) {
		openBasicStatementListScope();
	}

	public void openBasicStatementListScope() {
		boolean parent_static = this.scopes.peek().static_scope;
		this.scopes.push(new Scope(null, parent_static, Scope.Kind.BLOCKSTMT));
	}

	public void openScope(Statement stmt) {
		boolean parent_static = this.scopes.peek().static_scope;
		this.scopes.push(new Scope(null, parent_static, Scope.Kind.STMT));
	}

	public void closeScope() {
		this.scopes.pop();
	}

	public Scope getActiveScope() {
		return this.scopes.peek();
	}

	public boolean inStatic() {
		return this.getActiveScope().static_scope;
	}

	/**
	 * Add a declaration to the active scope, checking for scope related errors
	 * 
	 * @param decl
	 *            The declaration AST object to add
	 */
	public void declare(Declaration decl) {
		Scope scope = this.getActiveScope();
		if (scope.kind.depth >= Scope.Kind.BLOCKSTMT.depth) {
			// Check that local variable isn't hiding another local variable or
			// parameter
			for (Scope parent_scope : this.scopes) {
				if (parent_scope.kind.depth < Scope.Kind.METHOD.depth) {
					// Allow hiding of non-local and non-parameter variables
					break;
				}

				Declaration hiding = parent_scope.get(decl.name);
				if (hiding != null)
					throw new DuplicateDefinitionException(decl);
			}
		}

		// Normal scope add will check for clashes within the same scope
		scope.add(decl);
	}

	/**
	 * Find the declaration that matches the given identifier
	 * 
	 * @param ident
	 *            The identifier to lookup
	 */
	public Declaration lookup(Identifier ident) {
		Declaration decl = null;
		for (Scope s : this.scopes) {
			decl = s.get(ident.spelling);
			if (decl != null) {
				if (decl.being_declared)
					throw new UndefinedReferenceException(ident);
				else if (inStatic() && !decl.allowStaticReference())
					throw new StaticReferenceException(ident);
				else
					break;
			}
		}

		return decl;
	}

	public void addStringLiteral(StringLiteral slit) {
		StringLiteralDecl sld = this.stringLits.get(slit.spelling);
		if (sld == null) {
			sld = new StringLiteralDecl(slit.spelling);
			this.stringLits.put(slit.spelling, sld);
		}
		slit.decl = sld;
	}

	public StringLiteralDecl[] getStringLiteralDecls() {
		Object[] vals = stringLits.values().toArray();
		StringLiteralDecl[] sld = new StringLiteralDecl[vals.length];
		for (int i = 0; i < vals.length; i++) {
			sld[i] = (StringLiteralDecl) vals[i];
		}
		return sld;
	}

	public ClassDecl getCurrentClass() {
		for (Scope s : this.scopes) {
			if (s.kind == Scope.Kind.CLASS)
				return (ClassDecl) s.owner;
		}
		return null;
	}

	public MethodDecl getCurrentMethod() {
		for (Scope s : this.scopes) {
			if (s.kind == Scope.Kind.METHOD)
				return (MethodDecl) s.owner;
		}
		return null;
	}

	/**
	 * Try to get the class for the given identifier
	 * 
	 * @param ident
	 *            The identifier
	 * @return The class or null if not found
	 */
	public ClassDecl getClass(Identifier ident) {
		for (Scope s : this.scopes) {
			if (s.kind == Scope.Kind.PACKAGE || s.kind == Scope.Kind.PREDEFINED) {
				Declaration decl = s.get(ident.spelling);
				if (decl instanceof ClassDecl)
					return (ClassDecl) decl;
			}
		}
		return null;
	}
}
