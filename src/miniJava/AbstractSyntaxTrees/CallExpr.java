/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class CallExpr extends Expression
{
	public CallExpr(Reference f, ExprList el) {
		this(f, el, SourcePosition.ZERO);
	}
	
    public CallExpr(Reference f, ExprList el, SourcePosition posn){
        super(posn);
        methodRef = f;
        argList = el;
    }
        
    public <A,R> R visit(Visitor<A,R> v, A o) {
        return v.visitCallExpr(this, o);
    }
    
    public Reference methodRef;
    public ExprList argList;
}