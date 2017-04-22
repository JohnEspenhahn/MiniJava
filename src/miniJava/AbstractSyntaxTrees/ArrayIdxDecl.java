package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeDescription.RuntimeDescription;
import miniJava.SyntacticAnalyzer.SourcePosition;

/**
 * Special declaration for linking to an element of an array, rather than the array itself
 * @author jesp
 *
 */
public class ArrayIdxDecl extends Declaration {
	private Declaration arrDecl;
	
	public ArrayIdxDecl(Declaration arrDecl, ArrayType type, SourcePosition posn) {
		super("[...]", type.eltType, posn);
		this.arrDecl = arrDecl;
	}

	@Override
	public boolean allowStaticReference() {
		return false;
	}

	@Override
	public Object visit(Visitor v, Object o) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RuntimeDescription getRuntimeDesc() {
		// Array offset for indexing is handled in visitor
		return this.arrDecl.getRuntimeDesc();
	}

}
