/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class Declaration extends AST {
	
	public String name;
	public TypeDenoter type;
	
	public Declaration(String name, TypeDenoter type) {
		this(name, type, SourcePosition.ZERO);
	}
	
	public Declaration(String name, TypeDenoter type, SourcePosition posn) {
		super(posn);
		this.name = name;
		this.type = type;
		
		// Error flags
		this.duplicate_error = false;
		this.selfref_error = false;
		this.danglingdef_error = false;
	}
	
	public abstract Declaration getMember(String name);
	
	// Error flags
	public boolean duplicate_error;
	public boolean selfref_error;
	public boolean danglingdef_error;
}
