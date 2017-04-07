/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.ContextualAnalyzer.Exceptions.MiniJavaClassNotFoundException;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class ClassType extends TypeDenoter
{
	public Identifier className;
	private ClassDecl decl;
	
	public ClassType(Identifier cn) {
		this(cn, SourcePosition.ZERO);
	}
	
    public ClassType(Identifier cn, SourcePosition posn){
        super(TypeKind.CLASS, posn);
        className = cn;
    }
            
    public Object visit(Visitor v, Object o) {
        return v.visitClassType(this, o);
    }
    
    public void setDecl(ClassDecl decl) {
    	if (decl == null) throw new MiniJavaClassNotFoundException(this);
    	this.decl = decl;
    }
    
    public ClassDecl getDecl() {
    	return this.decl;
    }

	@Override
	public MemberDecl getMember(Identifier ident) {
		// Calling from an instance of the class, so allow access to non-statics
		return this.decl.getMember(ident, false);
	}

    
}
