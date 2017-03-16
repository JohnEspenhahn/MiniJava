/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class BaseType extends TypeDenoter
{
	public BaseType(TypeKind t) {
		this(t, SourcePosition.ZERO);
	}
	
    public BaseType(TypeKind t, SourcePosition posn){
        super(t, posn);
    }
    
    public <A,R> R visit(Visitor<A,R> v, A o) {
        return v.visitBaseType(this, o);
    }
    
    @Override
    public MemberDecl getMember(Identifier ident) {
    	// Base type has no members
    	return null;
    }
}
