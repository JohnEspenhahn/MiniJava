package miniJava.ContextualAnalyzer;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ArrayType;
import miniJava.AbstractSyntaxTrees.AssignStmt;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BinaryExpr;
import miniJava.AbstractSyntaxTrees.BlockStmt;
import miniJava.AbstractSyntaxTrees.BooleanLiteral;
import miniJava.AbstractSyntaxTrees.CallExpr;
import miniJava.AbstractSyntaxTrees.CallStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.IdRef;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.IfStmt;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.IxIdRef;
import miniJava.AbstractSyntaxTrees.IxQRef;
import miniJava.AbstractSyntaxTrees.LiteralExpr;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.NewArrayExpr;
import miniJava.AbstractSyntaxTrees.NewObjectExpr;
import miniJava.AbstractSyntaxTrees.NullLiteral;
import miniJava.AbstractSyntaxTrees.Operator;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.QRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.ReturnStmt;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;
import miniJava.ContextualAnalyzer.Exceptions.TypeException;

public class TypeVisitor implements Visitor<Object, Type> {
	private TypeErrors errors;

	public TypeErrors visit(Package prog) {
		this.errors = new TypeErrors();	
		visitPackage(prog, null);		
		return errors;
	}
	
	@Override
	public Type visitPackage(Package prog, Object arg) {
		for (ClassDecl decl: prog.classDeclList) {
			decl.visit(this, null);
		}
		return Type.UNSUPPORTED; // Always OK
	}

	@Override
	public Type visitClassDecl(ClassDecl cd, Object arg) {
		for (MethodDecl method: cd.methodDeclList) {
			method.visit(this, null);
		}
		return new Type(TypeKind.CLASS, cd);
	}

	@Override
	public Type visitFieldDecl(FieldDecl fd, Object arg) {
		return fd.type.visit(this, null);
	}

	@Override
	public Type visitMethodDecl(MethodDecl md, Object arg) {
		for (Statement s: md.statementList) {
			s.visit(this, null);
		}
		return Type.UNSUPPORTED;
	}

	@Override
	public Type visitParameterDecl(ParameterDecl pd, Object arg) {
		return pd.type.visit(this, null);
	}

	@Override
	public Type visitVarDecl(VarDecl decl, Object arg) {
		return decl.type.visit(this, null);
	}

	@Override
	public Type visitBaseType(BaseType type, Object arg) {
		return new Type(type.typeKind);
	}

	@Override
	public Type visitClassType(ClassType type, Object arg) {
		// Special case
		if (type.getDecl() == ScopeStack.UNSUPPORTED_STRING)
			return Type.UNSUPPORTED;
		
		return new Type(TypeKind.CLASS, type.getDecl());
	}

	@Override
	public Type visitArrayType(ArrayType type, Object arg) {
		return new Type(type.typeKind, type.eltType.visit(this, null));
	}

	@Override
	public Type visitBlockStmt(BlockStmt stmt, Object arg) {
		for (Statement s: stmt.sl) {
			s.visit(this, null);
		}
		return Type.UNSUPPORTED;
	}

	@Override
	public Type visitVardeclStmt(VarDeclStmt stmt, Object arg) {
		Type declType = stmt.varDecl.visit(this, null);
		Type expType = stmt.initExp.visit(this, null);
		return checkEquals(declType, expType, stmt);
	}

	@Override
	public Type visitAssignStmt(AssignStmt stmt, Object arg) {
		Type refType = stmt.ref.visit(this, null);
		Type expType = stmt.val.visit(this, null);
		return checkEquals(refType, expType, stmt);
	}

	@Override
	public Type visitCallStmt(CallStmt stmt, Object arg) {
		MethodDecl decl = (MethodDecl) stmt.methodRef.getDecl();
		if (decl.parameterDeclList.size() != stmt.argList.size()) {
			this.errors.add(new TypeException("Argument list length doesn't match declaration", stmt));
		} else {
			for (int i = 0; i < decl.parameterDeclList.size() && i < stmt.argList.size(); i++) {
				ParameterDecl p = decl.parameterDeclList.get(i);
				
				Type pType = p.visit(this, null);
				Type expType = stmt.argList.get(i).visit(this, null);
				checkEquals(pType, expType, stmt);
			}	
		}
		return decl.type.visit(this, null);
	}

	@Override
	public Type visitReturnStmt(ReturnStmt stmt, Object arg) {
		Type methodType = stmt.wrappingMethod.type.visit(this, null);
		if (stmt.returnExpr == null) {
			return checkEquals(methodType, Type.VOID, stmt);
		}
		
		Type returnType = stmt.returnExpr.visit(this, null);
		return checkEquals(methodType, returnType, stmt);
	}

	@Override
	public Type visitIfStmt(IfStmt stmt, Object arg) {
		stmt.cond.visit(this, null);
		stmt.thenStmt.visit(this, null);
		if (stmt.elseStmt != null)
			stmt.elseStmt.visit(this, null);
		return Type.UNSUPPORTED;
	}

	@Override
	public Type visitWhileStmt(WhileStmt stmt, Object arg) {
		stmt.cond.visit(this, null);
		stmt.body.visit(this, null);
		return Type.UNSUPPORTED;
	}

	@Override
	public Type visitUnaryExpr(UnaryExpr expr, Object arg) {
		Type exprType = expr.expr.visit(this, null);
		switch (expr.operator.kind) {
		case MINUS:
			checkEquals(exprType, Type.INT, expr);
			return Type.INT;
		case NOT:
			checkEquals(exprType, Type.BOOLEAN, expr);
			return Type.BOOLEAN;
		default:
			throw new RuntimeException("Unhandled unary operator " + expr.operator.spelling);
		}
	}

	@Override
	public Type visitBinaryExpr(BinaryExpr expr, Object arg) {
		Type leftType = expr.left.visit(this, null);
		Type rightType = expr.right.visit(this, null);
		switch (expr.operator.kind) {
		case OR: case AND:
			checkEquals(leftType, Type.BOOLEAN, expr);
			checkEquals(rightType, Type.BOOLEAN, expr);
			return Type.BOOLEAN;
		case EQU: case NOT_EQU:
			checkEquals(leftType, rightType, expr);
			return Type.BOOLEAN;
		case LSS: case GTR: case GTR_EQU: case LSS_EQU:
			checkEquals(leftType, Type.INT, expr);
			checkEquals(rightType, Type.INT, expr);
			return Type.BOOLEAN;
		case PLUS: case MINUS: case MULT: case DIV:
			checkEquals(leftType, Type.INT, expr);
			checkEquals(rightType, Type.INT, expr);
			return Type.INT;
		default:
			throw new RuntimeException("Unhandled binary operator " + expr.operator.spelling);
		}
	}

	@Override
	public Type visitRefExpr(RefExpr expr, Object arg) {
		return expr.ref.visit(this, null);
	}

	@Override
	public Type visitCallExpr(CallExpr expr, Object arg) {
		MethodDecl decl = (MethodDecl) expr.methodRef.getDecl();
		for (int i = 0; i < decl.parameterDeclList.size(); i++) {
			ParameterDecl p = decl.parameterDeclList.get(i);
			
			Type pType = p.visit(this, null);
			Type expType = expr.argList.get(i).visit(this, null);
			checkEquals(pType, expType, expr);
		}
		return decl.type.visit(this, null);
	}

	@Override
	public Type visitLiteralExpr(LiteralExpr expr, Object arg) {
		return expr.lit.visit(this, null);
	}

	@Override
	public Type visitNewObjectExpr(NewObjectExpr expr, Object arg) {
		return expr.classtype.visit(this, null);
	}

	@Override
	public Type visitNewArrayExpr(NewArrayExpr expr, Object arg) {
		Type elType = expr.eltType.visit(this, null);
		Type sizeType = expr.sizeExpr.visit(this, null);
		checkEquals(sizeType, Type.INT, expr);
		return new Type(TypeKind.ARRAY, elType);
	}

	@Override
	public Type visitThisRef(ThisRef ref, Object arg) {
		return ref.getDecl().type.visit(this, null);
	}

	@Override
	public Type visitIdRef(IdRef ref, Object arg) {
		Declaration decl = ref.getDecl();
		if (decl instanceof MethodDecl)
			return Type.UNSUPPORTED;
		else
			return decl.type.visit(this, null);
	}

	@Override
	public Type visitIxIdRef(IxIdRef ref, Object arg) {
		Type ixType = ref.indexExpr.visit(this, null);
		checkEquals(ixType, Type.INT, ref.indexExpr);
		
		Declaration decl = ref.getDecl();
		if (decl instanceof MethodDecl) return Type.UNSUPPORTED;
		else return decl.type.visit(this, null);
	}

	@Override
	public Type visitQRef(QRef ref, Object arg) {
		Declaration decl = ref.getDecl();
		if (decl instanceof MethodDecl) return Type.UNSUPPORTED;
		else return decl.type.visit(this, null);
	}

	@Override
	public Type visitIxQRef(IxQRef ref, Object arg) {
		Type ixType = ref.ixExpr.visit(this, null);
		checkEquals(ixType, Type.INT, ref.ixExpr);

		Declaration decl = ref.getDecl();
		if (decl instanceof MethodDecl) return Type.UNSUPPORTED;
		else return decl.type.visit(this, null);
	}

	@Override
	public Type visitIdentifier(Identifier id, Object arg) {
		throw new RuntimeException("Why am I visiting an Identifier?");
	}

	@Override
	public Type visitOperator(Operator op, Object arg) {
		return null;
	}

	@Override
	public Type visitIntLiteral(IntLiteral num, Object arg) {
		return Type.INT;
	}

	@Override
	public Type visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		return Type.BOOLEAN;
	}

	@Override
	public Type visitNullLiteral(NullLiteral nlit, Object arg) {
		return Type.NULL;
	}
	
	/**
	 * Check for type equality. Adds an error to this.errors if the are not equal
	 * @param t1 The main type
	 * @param t2 The secondary type
	 * @param ast The AST in which the types exist
	 * @return The type that results from an equality comparison of the types.
	 */
	public Type checkEquals(Type t1, Type t2, AST ast) {
		if (t1.kind == TypeKind.INT && t2.kind == TypeKind.INT) 
			return Type.INT;
		else if (t1.kind == TypeKind.BOOLEAN && t2.kind == TypeKind.BOOLEAN)
			return Type.BOOLEAN;
		else if (t1.kind == TypeKind.VOID && t2.kind == TypeKind.VOID)
			return Type.VOID;
		else if (t1.kind == TypeKind.ERROR)
			// ERROR equals anything
			return t2;
		else if (t2.kind == TypeKind.ERROR)
			return t1;
		else if (t1.kind == TypeKind.CLASS && t2.kind == TypeKind.CLASS && t1.decl == t2.decl)
			return t1;
		else if (t1.kind == TypeKind.CLASS && t2.kind == TypeKind.NULL)
			return Type.NULL;
		else if (t2.kind == TypeKind.CLASS && t1.kind == TypeKind.NULL)
			return Type.NULL;
		else if (t1.kind == TypeKind.NULL && t2.kind == TypeKind.NULL)
			return Type.NULL;
		else if (t1.kind == TypeKind.ARRAY && t2.kind == TypeKind.ARRAY)
			return checkEquals(t1.ixType, t2.ixType, ast);
		else {
			this.errors.add(new TypeException(t1, t2, ast));
			return Type.ERROR;
		}
	}

}
