package miniJava.ContextualAnalyzer;

import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.TypeKind;

public class Type {
	public static final Type UNSUPPORTED = new Type(TypeKind.UNSUPPORTED);
	public static final Type ERROR = new Type(TypeKind.ERROR);
	public static final Type INT = new Type(TypeKind.INT);
	public static final Type BOOLEAN = new Type(TypeKind.BOOLEAN);
	public static final Type VOID = new Type(TypeKind.VOID);
	public static final Type NULL = new Type(TypeKind.NULL);

	public TypeKind kind;
	public ClassDecl decl;
	public Type ixType;
	
	/**
	 * Type definition for primitive types
	 * @param kind The primitive type
	 */
	public Type(TypeKind kind) {
		this(kind, null, null);
	}
	
	/***
	 * Type definition for CLASS type
	 * @param kind TypeKind.CLASS
	 * @param decl The declaration for the class
	 */
	public Type(TypeKind kind, ClassDecl decl) {
		this(kind, decl, null);
	}
	
	/***
	 * Type definition for ARRAY type
	 * @param kind TypeKind.ARRAY
	 * @param ixType The type of the elements
	 */
	public Type(TypeKind kind, Type ixType) {
		this(kind, null, ixType);
	}
	
	public Type(TypeKind kind, ClassDecl decl, Type ixType) {
		this.kind = kind;
		this.decl = decl;
		this.ixType = ixType;
		
		if (kind == TypeKind.CLASS && decl == null) {
			throw new RuntimeException("All types of TypeKind CLASS must have a corresponding ClassDecl");
		} else if (kind == TypeKind.ARRAY && ixType == null) {
			throw new RuntimeException("All type of TypeKind ARRAY must have an index Type");
		}
	}
	
	@Override
	public String toString() {
		switch (kind) {
		case INT: return "int";
		case BOOLEAN: return "boolean";
		case NULL: return "null";
		case VOID: return "void";
		case UNSUPPORTED: return "unsupported";
		case ERROR: return "error";
		case CLASS: return decl.name;
		case ARRAY: return ixType.toString() + "[]";
		default: return "UNKNOWN TYPE " + this.kind;
		}
	}
	
}
