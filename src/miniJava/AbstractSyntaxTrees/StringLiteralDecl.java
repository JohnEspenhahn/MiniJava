package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.GlobalClasses;

public class StringLiteralDecl extends Declaration {

	public StringLiteralDecl(String str) {
		super(str, GlobalClasses.CLASSTYPE_STRING);
	}

	@Override
	public boolean allowStaticReference() {
		return true;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitStringLiteralDecl(this, o);
	}

}
