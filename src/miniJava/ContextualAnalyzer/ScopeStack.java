package miniJava.ContextualAnalyzer;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BlockStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.FieldDeclList;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodDeclList;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.ParameterDeclList;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.StatementList;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.ContextualAnalyzer.Exceptions.DuplicateDefinitionException;
import miniJava.ContextualAnalyzer.Exceptions.StaticReferenceException;
import miniJava.ContextualAnalyzer.Exceptions.UndefinedReferenceException;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class ScopeStack {

	// Stack of all current scopes
	private Deque<Scope> scopes;
	private Map<String, ClassDecl> classes;
	
	public ScopeStack() {
		// Use linked list to get correct iteration order
		this.scopes = new LinkedList<Scope>();
		this.classes = new HashMap<String, ClassDecl>();
		
		createLevel0();
	}
	
	private void createLevel0() {
		// Force level 0
		this.scopes.push(new Scope(null, true, Scope.Kind.PACKAGE));
		
		// class System
		FieldDeclList SystemFields = new FieldDeclList();
		SystemFields.add(new FieldDecl(false, true, 
				new ClassType(new Identifier(new Token(TokenKind.IDENTIFIER, "_PrintStream", null, null)), null), 
				"out", null));
		declare(new ClassDecl("System", SystemFields, new MethodDeclList()));
		
		// class _PrintStream
		MethodDeclList PrintStreamMethods = new MethodDeclList();
		ParameterDeclList PrintLnParams = new ParameterDeclList();
		PrintLnParams.add(new ParameterDecl(new BaseType(TypeKind.INT, null), "n"));
		PrintStreamMethods.add(new MethodDecl(
				new FieldDecl(false, false, new BaseType(TypeKind.VOID, null), "println"),
				PrintLnParams, new StatementList()));
		declare(new ClassDecl("_PrintStream", new FieldDeclList(), PrintStreamMethods));
		
		// class String
		declare(new ClassDecl("String", new FieldDeclList(), new MethodDeclList()));
	}
	
	public void openScope(ClassDecl decl) {
		this.scopes.push(new Scope(decl, true, Scope.Kind.CLASS));
	}
	
	public void openScope(MethodDecl decl) {
		this.scopes.push(new Scope(decl, decl.isStatic, Scope.Kind.METHOD));
	}
	
	public void openScope(BlockStmt decl) {
		openScope(decl.sl);
	}
	
	public void openScope(StatementList decl) {
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
	 * @param decl The declaration AST object to add
	 */
	public void declare(Declaration decl) {		
		// Keep track of all ClassDecls
		if (decl instanceof ClassDecl && !classes.containsKey(decl.name))
			this.classes.put(decl.name, (ClassDecl) decl);
		
		Scope scope = this.getActiveScope();
		if (scope.kind.depth >= Scope.Kind.BLOCKSTMT.depth) {
			// Check that local variable isn't hiding another local variable or parameter
			for (Scope parent_scope: this.scopes) {
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
	 * Link the given identifier to the matching declaration with the given name
	 * @param ref The reference to get linked
	 * @param name The name of the declared variable to link it to
	 */
	public Declaration lookup(Identifier ident) {
		Declaration decl = null;
		for (Scope s: this.scopes) {
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
	
	public Declaration getCurrentClass() {
		for (Scope s: this.scopes) {
			if (s.kind == Scope.Kind.CLASS)
				return s.owner;
		}
		return null;
	}
	
	public ClassDecl getClass(Identifier ident) {
		return this.classes.get(ident.spelling);
	}
}
