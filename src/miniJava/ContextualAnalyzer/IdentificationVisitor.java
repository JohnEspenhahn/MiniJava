package miniJava.ContextualAnalyzer;

import miniJava.AbstractSyntaxTrees.ArrayIdxDecl;
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
import miniJava.AbstractSyntaxTrees.Expression;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.IdRef;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.IfStmt;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.IxIdRef;
import miniJava.AbstractSyntaxTrees.IxQRef;
import miniJava.AbstractSyntaxTrees.LiteralExpr;
import miniJava.AbstractSyntaxTrees.MemberDecl;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.NewArrayExpr;
import miniJava.AbstractSyntaxTrees.NewObjectExpr;
import miniJava.AbstractSyntaxTrees.NullLiteral;
import miniJava.AbstractSyntaxTrees.Operator;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.QRef;
import miniJava.AbstractSyntaxTrees.QualifiedRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.ReturnStmt;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.ThisDecl;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;
import miniJava.ContextualAnalyzer.Exceptions.ArrayIdentifictionException;
import miniJava.ContextualAnalyzer.Exceptions.IdentificationException;
import miniJava.ContextualAnalyzer.Exceptions.LefthandThisException;
import miniJava.ContextualAnalyzer.Exceptions.NotVisibleException;
import miniJava.ContextualAnalyzer.Exceptions.ReadonlyAssignmentException;
import miniJava.ContextualAnalyzer.Exceptions.ReturnMissingException;
import miniJava.ContextualAnalyzer.Exceptions.StaticThisException;
import miniJava.ContextualAnalyzer.Exceptions.UndefinedReferenceException;

public class IdentificationVisitor extends Visitor {
	private ScopeStack scope;
	
	public boolean visit(Package prog) {
		try {
			scope = new ScopeStack();
			
			// Visit package classes
			visitPackage(prog, null);
			return true;
		} catch (IdentificationException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	@Override
	public Object visitPackage(Package prog, Object arg) {
		if (scope == null) scope = new ScopeStack();
		
		for (ClassDecl c: prog.classDeclList)
			scope.declare(c);
		
		// Visit types of all publicly accessible variables in each class (don't add to scope yet)
		for (ClassDecl c: prog.classDeclList) {
			for (FieldDecl fd: c.fieldDeclList)
				fd.type.visit(this, scope);
			for (MethodDecl md: c.methodDeclList)
				md.type.visit(this, scope);
		}
		
		// Visit all publicly accessible classes
		for (ClassDecl c: prog.classDeclList)
			c.visit(this, scope);
		
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg) {
		scope.openScope(cd);
		
		cd.type.visit(this, scope);
		for (FieldDecl fd: cd.fieldDeclList)
			scope.declare(fd);
		for (MethodDecl md: cd.methodDeclList)
			scope.declare(md);
		
		for (FieldDecl fd: cd.fieldDeclList)
			fd.visit(this, scope);
		for (MethodDecl md: cd.methodDeclList)
			md.visit(this, scope);
		
		scope.closeScope();
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg) {
		fd.type.visit(this, scope);
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg) {
		scope.openScope(md);
		for (ParameterDecl pd: md.parameterDeclList)
			scope.declare(pd);
		for (ParameterDecl pd: md.parameterDeclList)
			pd.visit(this, scope);
		
		scope.openScope(md.statementList);
		for (Statement s: md.statementList) {
			s.visit(this, scope);
		}
		
		if (md.statementList.size() > 0) {
			Statement last = md.statementList.get(md.statementList.size()-1);
			checkReturn(md, last, scope);
		} else {
			checkReturn(md, null, scope);
		}
		scope.closeScope(); // statementList
		
		scope.closeScope(); // Method decl
		return null;
	}
	
	private void checkReturn(MethodDecl md, Statement last, Object arg) {
		boolean isVoid = (md.type.typeKind == TypeKind.VOID);
		if (last == null || !(last instanceof ReturnStmt)) {
			if (isVoid) {
				ReturnStmt rtn = new ReturnStmt(null);
				md.statementList.add(rtn);
				
				rtn.visit(this, scope);
			} else throw new ReturnMissingException(md, md.posn);
		}
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg) {
		pd.type.visit(this, scope);
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg) {
		scope.declare(decl);
		decl.type.visit(this, scope);
		return null;
	}
	
	@Override
	public Object visitThisDecl(ThisDecl decl, Object arg) {
		return null; // Copied type from class, so don't need to visit
	}
	
	@Override
	public Object visitArrayIdxDecl(ArrayIdxDecl decl, Object arg) {
		return null; // Copied type from array, so don't need to visit
	}

	@Override
	public Object visitBaseType(BaseType type, Object arg) {
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg) {
		// Only link class types to class declarations
		ClassDecl classDecl = scope.getClass(type.className);
		type.setDecl(classDecl);
		return null;
	}

	@Override
	public Object visitArrayType(ArrayType type, Object arg) {
		type.eltType.visit(this, scope);
		return null;
	}

	@Override
	public Object visitBlockStmt(BlockStmt stmt, Object arg) {
		scope.openScope(stmt);
		for (Statement s: stmt.sl)
			s.visit(this, scope);
		scope.closeScope();
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg) {
		stmt.varDecl.visit(this, scope);
		stmt.varDecl.being_declared = true;		
		stmt.initExp.visit(this, scope);
		stmt.varDecl.being_declared = false;
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg) {
		if (stmt.ref instanceof ThisRef)
			throw new LefthandThisException((ThisRef) stmt.ref);
		
		stmt.ref.visit(this, scope); // left-hand
		if (stmt.ref.getDecl() instanceof MethodDecl)
			throw new ReadonlyAssignmentException(stmt.ref);
		else if (stmt.ref.getDecl() instanceof MemberDecl)
			if (((MemberDecl) stmt.ref.getDecl()).isReadonly)
				throw new ReadonlyAssignmentException(stmt.ref);
		
		stmt.val.visit(this, scope); // right-hand
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg) {
		stmt.methodRef.visit(this, scope);
		for (Expression e: stmt.argList)
			e.visit(this, scope);
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg) {
		stmt.wrappingMethod = scope.getCurrentMethod();
		if (stmt.returnExpr != null) stmt.returnExpr.visit(this, scope);	
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg) {
		stmt.cond.visit(this, scope);
		
		scope.openScope(stmt.thenStmt);
		stmt.thenStmt.visit(this, scope);
		scope.closeScope();
		
		if (stmt.elseStmt != null) {
			scope.openScope(stmt.elseStmt);
			stmt.elseStmt.visit(this, scope);
			scope.closeScope();
		}
		
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg) {
		stmt.cond.visit(this, scope);
		
		scope.openScope(stmt.body);
		stmt.body.visit(this, scope);
		scope.closeScope();
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg) {
		expr.expr.visit(this, scope);
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg) {
		expr.left.visit(this, scope);
		expr.right.visit(this, scope);
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg) {
		expr.ref.visit(this, scope);
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg) {
		expr.methodRef.visit(this, scope);
		for (Expression e: expr.argList)
			e.visit(this, scope);
		return null;
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg) {
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg) {
		expr.classtype.visit(this, scope);
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg) {
		expr.eltType.visit(this, scope);
		expr.sizeExpr.visit(this, scope);
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg) {
		// Don't allow 'this' in static scopes
		if (scope.inStatic()) throw new StaticThisException(ref);
		
		ref.setDecl(new ThisDecl((ClassType) scope.getCurrentClass().type));
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg) {
		ref.setDecl(scope.lookup(ref.id));
		return null;
	}

	@Override
	public Object visitIxIdRef(IxIdRef ref, Object arg) {
		Declaration decl = scope.lookup(ref.id);
		if (!(decl.type instanceof ArrayType)) throw new ArrayIdentifictionException(ref);
		
		// Want to link to element of array, not array itself
		ref.setDecl(new ArrayIdxDecl(decl, (ArrayType) decl.type, ref.posn));
		
		ref.indexExpr.visit(this, scope);
		return null;
	}

	@Override
	public Object visitQRef(QRef ref, Object arg) {
		ref.ref.visit(this, scope); // Bottom up
		
		if (ref.ref.getDecl() == null) throw new UndefinedReferenceException(ref.ref);
		MemberDecl member = ref.ref.getDecl().getMember(ref.id);
		ref.setDecl(member);
		
		if (member.isPrivate) checkPrivate(ref, member, scope);
		
		return null;
	}

	@Override
	public Object visitIxQRef(IxQRef ref, Object arg) {
		ref.ref.visit(this, scope); // Bottom up
		
		if (ref.ref.getDecl() == null) throw new UndefinedReferenceException(ref.ref);
		MemberDecl member = ref.ref.getDecl().getMember(ref.id);
		if (!(member.type instanceof ArrayType)) throw new ArrayIdentifictionException(ref);
		
		// Want to link to element of array, not array itself
		ref.setDecl(new ArrayIdxDecl(member, (ArrayType) member.type, ref.posn));
		
		if (member.isPrivate) checkPrivate(ref, member, scope);		
		
		ref.ixExpr.visit(this, scope);
		return null;
	}
	
	private void checkPrivate(QualifiedRef ref, MemberDecl member, Object arg) {
		if (member.isPrivate) {
			// Check if this member is the member from the current class with the same name (allow nonstatic)
			MemberDecl currentclass_member = scope.getCurrentClass().getMember(ref.getIdent(), false);
			if (member != currentclass_member)
				throw new NotVisibleException(ref.getRef(), member);
		}
	}

	@Override
	public Object visitIdentifier(Identifier id, Object arg) {
		throw new RuntimeException("Why am I visiting an Identifier?");
	}

	@Override
	public Object visitOperator(Operator op, Object arg) {
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg) {
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		return null;
	}

	@Override
	public Object visitNullLiteral(NullLiteral nlit, Object arg) {
		return null;
	}

}
