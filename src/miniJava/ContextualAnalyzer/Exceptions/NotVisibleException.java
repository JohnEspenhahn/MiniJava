package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.MemberDecl;
import miniJava.AbstractSyntaxTrees.Reference;

@SuppressWarnings("serial")
public class NotVisibleException extends IdentificationException {

	public NotVisibleException(Reference ref, MemberDecl member) {
		super("The member " + member.name + " in the reference " + ref + " is not visible", ref.posn);
	}
	
}
