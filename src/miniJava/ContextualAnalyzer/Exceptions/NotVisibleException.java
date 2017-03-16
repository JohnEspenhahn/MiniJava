package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.MemberDecl;
import miniJava.AbstractSyntaxTrees.Reference;

public class NotVisibleException extends IdentificationException {
	private static final long serialVersionUID = -6820010753287264188L;

	public NotVisibleException(Reference ref, MemberDecl member) {
		super("The member " + member.name + " in the reference " + ref + " is not visible", ref.posn);
	}
	
}
