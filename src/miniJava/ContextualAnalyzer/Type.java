package miniJava.ContextualAnalyzer;

import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.TypeKind;

public class Type {
	public static Type UNSUPPORTED;
	public static Type ERROR;
	public static Type INT;
	public static Type BOOLEAN;
	public static Type VOID;
	public static Type NULL;
	
	static {
		UNSUPPORTED = new Type(TypeKind.UNSUPPORTED);
		ERROR = new Type(TypeKind.ERROR);
		INT = new Type(TypeKind.INT);
		BOOLEAN = new Type(TypeKind.BOOLEAN);
		VOID = new Type(TypeKind.VOID);
		NULL = new Type(TypeKind.NULL);
	}

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
		return kind.toString();
	}
	
}
