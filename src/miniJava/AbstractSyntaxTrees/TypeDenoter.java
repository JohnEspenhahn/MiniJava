/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

abstract public class TypeDenoter extends AST {
    
	public TypeKind typeKind;
	
    public TypeDenoter(TypeKind type, SourcePosition posn){
        super(posn);
        typeKind = type;
    }
    
    /**
     * Get a member of this type
     * @param id The identifier of the member
     * @return The member or null if not found
     */
    public abstract MemberDecl getMember(Identifier id);
    
}

        