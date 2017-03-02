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
	
	public ClassType(Identifier cn) {
		this(cn, SourcePosition.ZERO);
	}
	
    public ClassType(Identifier cn, SourcePosition posn){
        super(TypeKind.CLASS, posn);
        className = cn;
    }
            
    public <A,R> R visit(Visitor<A,R> v, A o) {
        return v.visitClassType(this, o);
    }

	@Override
	public Declaration getMember(String name) {
		Declaration classDecl = this.className.getDecl();
		if (classDecl == null) throw new MiniJavaClassNotFoundException(this);
		
		return classDecl.getMember(name);
	}

    
}
