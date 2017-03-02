package miniJava.ContextualAnalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BlockStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.FieldDeclList;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.MemberDecl;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodDeclList;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.ParameterDeclList;
import miniJava.AbstractSyntaxTrees.Reference;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.StatementList;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.ContextualAnalyzer.Exceptions.UndefinedReferenceException;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class ScopeStack {

	// Stack of all current scopes
	private Stack<Scope> scopes;
	private Map<String, ClassDecl> classes;
	
	// Variables that are being declared (to detect self-reference)
	private Set<String> being_declared;
	
	public ScopeStack() {
		this.scopes = new Stack<Scope>();
		this.classes = new HashMap<String, ClassDecl>();
		
		this.being_declared = new HashSet<String>();
		
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
	
	public void startDeclaring(VarDecl decl) {
		this.being_declared.add(decl.name);
	}
	
	public void declare(Declaration decl) {
		// Require calling startDeclaring before declare for VarDecls (needed for self-reference error)
		if (decl instanceof VarDecl && !this.being_declared.contains(decl.name))
			throw new RuntimeException("VarDecl " + decl + " was not started!");
		
		// Keep track of all ClassDecls
		if (decl instanceof ClassDecl && !classes.containsKey(decl.name))
			this.classes.put(decl.name, (ClassDecl) decl);
		
		Scope scope = this.getActiveScope();
		if (scope.kind.depth >= Scope.Kind.BLOCKSTMT.depth) {
			// Check that local variable isn't hiding another local variable
			for (Scope parent_scope: this.scopes) {
				if (parent_scope.kind.depth < Scope.Kind.METHOD.depth) {
					// Allow hiding class or class member
					break;
				} 
				
				Declaration hiding = parent_scope.get(decl.name);
				if (hiding != null) {
					// Found hiding of another local variable
					hiding.duplicate_error = decl.duplicate_error = true;
					break;
				}					
			}
		}
		
		// Normal scope add will check for clashes within the same scope
		scope.add(decl);
	}
	
	public void link(Reference ref, String name) {
		Declaration decl = null;
		for (Scope s: this.scopes) {
			decl = s.get(name);
			if (decl != null) {
				// Check for selfreference error
				if (this.being_declared.contains(name))
					decl.selfref_error = true;
				// Found match, so stop looking
				break;
			}
		}
		
		link(ref, decl);
	}
	
	public void link(Reference ref, Declaration decl) {
		if (decl == null) throw new UndefinedReferenceException(ref);
		
		// Don't allow accessing non-static members in a static context
		if (decl instanceof MemberDecl && inStatic() && !((MemberDecl) decl).isStatic)
			ref.illegal_nonstatic_error = true;
		
		ref.setDecl(decl);
	}
	
	public void finishDeclaring(VarDecl decl) {
		this.being_declared.remove(decl.name);
	}
	
	public Declaration getCurrentClass() {
		for (Scope s: this.scopes) {
			if (s.kind == Scope.Kind.CLASS)
				return s.owner;
		}
		return null;
	}
	
	public ClassDecl getClass(String name) {
		return this.classes.get(name);
	}
}
