package miniJava.ContextualAnalyzer;

import java.util.HashMap;
import java.util.Map;

import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodOverloadDecl;
import miniJava.ContextualAnalyzer.Exceptions.DanglingDefinitionException;
import miniJava.ContextualAnalyzer.Exceptions.DuplicateDefinitionException;

public class Scope {
	private Map<String, Declaration> decls;
	public boolean static_scope;
	public Scope.Kind kind;
	protected Declaration owner;
	
	public Scope(Declaration owner, boolean static_scope, Scope.Kind kind) {
		this.owner = owner;
		
		this.static_scope = static_scope;
		this.kind = kind;
		
		this.decls = new HashMap<String, Declaration>();
	}
	
	public void validateOverloading(TypeVisitor visitor) {
		// Only used for classes, could be better, but whatever
		Object[] ds = decls.values().toArray();
		for (int i = 0; i < ds.length; i++) {
			Declaration d = (Declaration) ds[i];
			if (d instanceof MethodOverloadDecl) {
				MethodOverloadDecl mod = (MethodOverloadDecl) d;				
				mod.buildLookupTree(visitor);
			}
		}
	}
	
	public void add(Declaration decl) {
		// Cannot declare variable in a stmt that just ends (needs to be at least a BlockStmt)
		if (kind == Kind.STMT) throw new DanglingDefinitionException(decl);
		
		Declaration prev = decls.get(decl.name);
		if (prev != null)
			// Check for overloading
			if (prev instanceof MethodDecl && decl instanceof MethodDecl) {
				this.decls.put(decl.name, new MethodOverloadDecl((MethodDecl) prev, (MethodDecl) decl));
			} else if (prev instanceof MethodOverloadDecl && decl instanceof MethodDecl) {
				((MethodOverloadDecl) prev).addMethod((MethodDecl) decl);
			} else {
				throw new DuplicateDefinitionException(decl);
			}
		else
			this.decls.put(decl.name, decl);
	}

	public Declaration get(String name) {
		return this.decls.get(name);
	}
	
	public enum Kind {
		PREDEFINED(-1), PACKAGE(0), CLASS(1), METHOD(2), BLOCKSTMT(3), STMT(4);
		
		public int depth;
		Kind(int depth) {
			this.depth = depth;
		}
	}
}
