/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class NewArrayExpr extends NewExpr
{
	public NewArrayExpr(TypeDenoter et, Expression e) {
		this(et, e, SourcePosition.ZERO);
	}
	
    public NewArrayExpr(TypeDenoter et, Expression e, SourcePosition posn){
        super(posn);
        eltType = et;
        sizeExpr = e;
    }
    
    public Object visit(Visitor v, Object o) {
        return v.visitNewArrayExpr(this, o);
    }

    public TypeDenoter eltType;
    public Expression sizeExpr;
}