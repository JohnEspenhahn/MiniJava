package miniJava.AbstractSyntaxTrees;

import java.util.ArrayList;

import miniJava.ContextualAnalyzer.Type;
import miniJava.ContextualAnalyzer.TypeVisitor;

public class MethodOverloadDecl extends MethodDecl {
	private ArrayList<MethodDecl> mds;

	public MethodOverloadDecl(ArrayList<MethodDecl> mds) {	
		super(mds.get(0).md, null, null);
		
		this.mds = mds;
	}
	
	public MethodDecl getMethod(Type[] types, TypeVisitor visitor) {
		for (int i = 0; i < mds.size(); i++) {
			MethodDecl md = mds.get(i);
			if (md.getParamCount() != types.length) continue;
			
			boolean match = true;
			Type pType = types[i];
			for (int j = 0; j < md.getParamCount(); j++) {
				ParameterDecl pd = md.getParameter(j);
				Type type = (Type) pd.visit(visitor, null);
				Type res = TypeVisitor.staticCheckEquals(type, pType);
				if (res == Type.ERROR) {
					match = false;
					break;
				}
			}
			
			if (match) {
				return md;
			}
		}
		
		return null;
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
	
}
