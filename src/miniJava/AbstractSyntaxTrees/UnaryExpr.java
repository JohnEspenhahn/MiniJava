/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class UnaryExpr extends Expression
{
	public UnaryExpr(Operator o, Expression e) {
		this(o, e, SourcePosition.ZERO);
	}
	
    public UnaryExpr(Operator o, Expression e, SourcePosition posn){
        super(posn);
        operator = o;
        expr = e;
    }
        
    public Object visit(Visitor v, Object o) {
        return v.visitUnaryExpr(this, o);
    }

    public Operator operator;
    public Expression expr;
}