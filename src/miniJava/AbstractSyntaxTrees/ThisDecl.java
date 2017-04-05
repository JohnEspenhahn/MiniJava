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
	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitThisDecl(this, o);
	}

}
