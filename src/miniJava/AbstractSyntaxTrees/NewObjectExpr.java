/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class NewObjectExpr extends NewExpr
{
	public NewObjectExpr(ClassType ct) {
		this(ct, SourcePosition.ZERO);
	}
	
    public NewObjectExpr(ClassType ct, SourcePosition posn){
        super(posn);
        classtype = ct;
    }
        
    public Object visit(Visitor v, Object o) {
        return v.visitNewObjectExpr(this, o);
    }
    
    public ClassType classtype;
}
