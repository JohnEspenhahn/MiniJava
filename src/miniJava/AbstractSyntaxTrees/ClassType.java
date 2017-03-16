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
            
    public <A,R> R visit(Visitor<A,R> v, A o) {
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
		// Unlike in the ClassDecl getMember function, here we have an instance to allow non-static
		for (FieldDecl f: decl.fieldDeclList)
			if (f.name.equals(ident.spelling))
				return f;
		
		for (MethodDecl m: decl.methodDeclList)
			if (m.name.equals(ident.spelling))
				return m;
		
		return null;
	}

    
}
