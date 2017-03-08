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
	public Declaration getMember(Identifier ident) {
		Declaration cd = this.className.getDecl();
		if (cd == null || !(cd instanceof ClassDecl))
			throw new MiniJavaClassNotFoundException(this);
		
		// Unlike in the ClassDecl getMember function, here we have an instance to allow non-static
		for (FieldDecl f: ((ClassDecl) cd).fieldDeclList)
			if (f.name.equals(ident.spelling))
				return f;
		
		for (MethodDecl m: ((ClassDecl) cd).methodDeclList)
			if (m.name.equals(ident.spelling))
				return m;
		
		return null;
	}

    
}
