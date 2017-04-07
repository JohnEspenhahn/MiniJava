package miniJava.AbstractSyntaxTrees;

public class ThisDecl extends Declaration {

	public ThisDecl(ClassType type) {
		super("this", type);
	}

	@Override
	public boolean allowStaticReference() {
		return false;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitThisDecl(this, o);
	}

}
