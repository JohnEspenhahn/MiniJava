package miniJava.ContextualAnalyzer.Exceptions;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.ContextualAnalyzer.Type;

@SuppressWarnings("serial")
public class TypeException extends RuntimeException {

	public TypeException(Type t1, Type t2, AST ast) {
		this("Incompatible types " + t1 + " and " + t2, ast);
	}
	
	public TypeException(String mss, AST ast) {
		super("*** " + mss + " at line " + ast.posn.getLine());
	}
}
