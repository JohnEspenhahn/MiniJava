package miniJava.ContextualAnalyzer;

import java.util.HashMap;
import java.util.Map;

import miniJava.AbstractSyntaxTrees.Declaration;

public class Scope {
	private Map<String, Declaration> decls;
	public final boolean static_scope;
	public final Scope.Kind kind;
	protected Declaration owner;
	
	public Scope(Declaration owner, boolean static_scope, Scope.Kind kind) {
		this.owner = owner;
		
		this.static_scope = static_scope;
		this.kind = kind;
		
		this.decls = new HashMap<String, Declaration>();
	}
	
	public void add(Declaration decl) {
		// Cannot declare variable in a stmt that just ends (needs to be at least a BlockStmt)
		if (kind == Kind.STMT)
			decl.danglingdef_error = true;
		
		Declaration prev = decls.get(decl.name);
		if (prev != null)
			// Flag both as duplicate
			prev.duplicate_error = decl.duplicate_error = true;
		else
			this.decls.put(decl.name, decl);
	}

	public Declaration get(String name) {
		return this.decls.get(name);
	}
	
	public enum Kind {
		PACKAGE(0), CLASS(1), METHOD(2), BLOCKSTMT(3), STMT(4);
		
		public final int depth;
		Kind(int depth) {
			this.depth = depth;
		}
	}
}
