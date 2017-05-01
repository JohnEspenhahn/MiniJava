package miniJava.AbstractSyntaxTrees;

import java.util.ArrayList;
import java.util.HashMap;

import miniJava.ContextualAnalyzer.Type;
import miniJava.ContextualAnalyzer.TypeVisitor;
import miniJava.ContextualAnalyzer.Exceptions.TypeException;

public class MethodOverloadDecl extends MethodDecl {
	private ArrayList<MethodDecl> mds;
	MethodOverloadNode root;
	
	public MethodOverloadDecl(MethodDecl m1, MethodDecl m2) {	
		super(m1, null, null);
		
		this.mds = new ArrayList<MethodDecl>();
		this.mds.add(m1); // Defining method
		
		addMethod(m2);
	}
	
	public void addMethod(MethodDecl m) {
		this.mds.add(m);
	}
	
	public int getMethodCount() {
		return mds.size();
	}
	
	public MethodDecl getMethod(int i) {
		return mds.get(i);
	}
	
	public MethodDecl getMethod(Type[] types) {
		if (root == null) throw new RuntimeException("Tried to get method before building lookup tree");
		return root.get(types);
	}
	
	@Override
	public int getParamCount() {
		throw new RuntimeException();
	}
	
	@Override
	public ParameterDecl getParameter(int i) {
		throw new RuntimeException();
	}
	
	@Override
	public int getStmtCount() {
		throw new RuntimeException();
	}
	
	@Override
	public Statement getStatement(int i) {
		throw new RuntimeException();
	}
	
	@Override
	public void appendStatement(Statement s) {
		throw new RuntimeException();
	}
 
	public void buildLookupTree(TypeVisitor visitor) {
		// Check return types equal
		Type t1 = null;
		root = new MethodOverloadNode(visitor);
		for (int i = 0; i < getMethodCount(); i++) {
			MethodDecl md = getMethod(i);
			
			// Check return type
			Type returnType = (Type) md.visit(visitor, null);
			if (t1 == null) t1 = returnType;
			else visitor.checkEquals(t1, returnType, md);
			
			root.put(md);
		}
	}
	
}

class MethodOverloadNode {
	private HashMap/* <String, MethodOverloadNode> */ lookup_tree;
	private TypeVisitor visitor;
	
	public MethodOverloadNode(TypeVisitor visitor) {
		this.lookup_tree = new HashMap();
		this.visitor = visitor;
	}
	
	public MethodOverloadNode(TypeVisitor visitor, MethodDecl value) {
		this(visitor);
		
		// Put default value (no need to check for prior existance here)
		this.lookup_tree.put("", value);
	}
	
	public void put(MethodDecl decl) {
		int pCnt = decl.getParamCount();
		if (pCnt == 0)
			putValue(decl);
		else
			put(decl, 0, pCnt);
	}
	
	public MethodDecl get(Type[] types) {
		return get(types, 0);
	}
	
	private MethodDecl get(Type[] types, int idx) {
		if (idx == types.length) {
			return (MethodDecl) lookup_tree.get("");
		}
		
		String typeName = types[idx].toString();
		Object node = lookup_tree.get(typeName);
		if (node == null) return null;
		return ((MethodOverloadNode) node).get(types, idx+1);
	}
	
	/**
	 * Try to put a default value for this level
	 * @param value The default value
	 */
	private void putValue(MethodDecl value) {
		Object node = lookup_tree.get("");
		if (node == null) {
			lookup_tree.put("", value);
		} else {
			throw new TypeException("Duplicate definition of " + value.name + " with these parameters", value);
		}
	}
	
	/**
	 * Put for a specific parameter index
	 * @param decl The decl trying to be put
	 * @param pIdx The parameter index
	 * @param maxIdx The maximum parameter index
	 */
	private void put(MethodDecl decl, int pIdx, int maxIdx) {
		String typeName = decl.getParameter(pIdx).visit(visitor, null).toString();
		Object node = lookup_tree.get(typeName);
		if (pIdx+1 >= maxIdx) {
			// Terminal
			if (node == null) {
				// Create layer and add
				lookup_tree.put(typeName, new MethodOverloadNode(visitor, decl));
			} else {
				// Type to add as default to layer
				((MethodOverloadNode) node).putValue(decl);
			}
		} else {
			// Not at end of parameter list yet, go to next
			if (node == null) {
				MethodOverloadNode next = new MethodOverloadNode(visitor);
				lookup_tree.put(typeName, next);
				next.put(decl, pIdx+1, maxIdx);
			} else {
				((MethodOverloadNode) node).put(decl, pIdx+1, maxIdx);
			}
		}
	}
}






