/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class AssignStmt extends Statement
{
	public AssignStmt(Reference r, Expression e) {
		this(r, e, SourcePosition.ZERO);
	}
	
    public AssignStmt(Reference r, Expression e, SourcePosition posn){
        super(posn);
        ref = r;
        val = e;
    }
    
    public Object visit(Visitor v, Object o) {
        return v.visitAssignStmt(this, o);
    }
    
    public Reference ref;
    public Expression val;
}