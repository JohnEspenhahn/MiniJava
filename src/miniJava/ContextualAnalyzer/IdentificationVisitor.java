package miniJava.ContextualAnalyzer;

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
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.ReturnStmt;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;
import miniJava.ContextualAnalyzer.Exceptions.IdentificationException;
import miniJava.ContextualAnalyzer.Exceptions.NotVisibleException;
import miniJava.ContextualAnalyzer.Exceptions.StaticThisException;
import miniJava.ContextualAnalyzer.Exceptions.UndefinedReferenceException;

public class IdentificationVisitor implements Visitor<ScopeStack, Object> {
	
	public boolean visit(Package prog) {
		try {
			ScopeStack scope = new ScopeStack();
			
			// Visit predefined classes
			visitClassDeclList(scope.getClasses(), scope);
			
			// Visit package classes
			visitPackage(prog, scope);
			return true;
		} catch (IdentificationException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	@Override
	public Object visitPackage(Package prog, ScopeStack scope) {
		if (scope == null) scope = new ScopeStack();
		
		visitClassDeclList(prog.classDeclList, scope);
		
		return null;
	}
	
	private void visitClassDeclList(Iterable<ClassDecl> cdl, ScopeStack scope) {
		for (ClassDecl c: cdl)
			scope.declare(c);
		
		// Visit all publicly accessible classes
		for (ClassDecl c: cdl)
			c.visit(this, scope);
		
		// Visit types of all publicly accessible variables in each class (don't add to scope yet)
		for (ClassDecl c: cdl) {
			for (FieldDecl fd: c.fieldDeclList)
				fd.type.visit(this, scope);
			for (MethodDecl md: c.methodDeclList)
				md.type.visit(this, scope);
		}
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, ScopeStack scope) {
		scope.openScope(cd);
		
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
	public Object visitFieldDecl(FieldDecl fd, ScopeStack scope) {
		fd.type.visit(this, scope);
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, ScopeStack scope) {
		scope.openScope(md);
		for (ParameterDecl pd: md.parameterDeclList)
			scope.declare(pd);
		for (ParameterDecl pd: md.parameterDeclList)
			pd.visit(this, scope);
		
		scope.openScope(md.statementList);
		for (Statement s: md.statementList)
			s.visit(this, scope);
		scope.closeScope();
		
		scope.closeScope();
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, ScopeStack scope) {
		pd.type.visit(this, scope);
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, ScopeStack scope) {
		scope.declare(decl);
		decl.type.visit(this, scope);
		return null;
	}

	@Override
	public Object visitBaseType(BaseType type, ScopeStack scope) {
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, ScopeStack scope) {
		// Only link class types to class declarations
		ClassDecl classDecl = scope.getClass(type.className);
		type.setDecl(classDecl);
		return null;
	}

	@Override
	public Object visitArrayType(ArrayType type, ScopeStack scope) {
		type.eltType.visit(this, scope);
		return null;
	}

	@Override
	public Object visitBlockStmt(BlockStmt stmt, ScopeStack scope) {
		scope.openScope(stmt);
		for (Statement s: stmt.sl)
			s.visit(this, scope);
		scope.closeScope();
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, ScopeStack scope) {
		stmt.varDecl.visit(this, scope);
		stmt.varDecl.being_declared = true;		
		stmt.initExp.visit(this, scope);
		stmt.varDecl.being_declared = false;
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, ScopeStack scope) {
		stmt.ref.visit(this, scope); // left-hand
		stmt.val.visit(this, scope); // right-hand
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, ScopeStack scope) {
		stmt.methodRef.visit(this, scope);
		for (Expression e: stmt.argList)
			e.visit(this, scope);
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, ScopeStack scope) {
		stmt.wrappingMethod = scope.getCurrentMethod();
		stmt.returnExpr.visit(this, scope);	
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, ScopeStack scope) {
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
	public Object visitWhileStmt(WhileStmt stmt, ScopeStack scope) {
		stmt.cond.visit(this, scope);
		
		scope.openScope(stmt.body);
		stmt.body.visit(this, scope);
		scope.closeScope();
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, ScopeStack scope) {
		expr.expr.visit(this, scope);
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, ScopeStack scope) {
		expr.left.visit(this, scope);
		expr.right.visit(this, scope);
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, ScopeStack scope) {
		expr.ref.visit(this, scope);
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, ScopeStack scope) {
		expr.methodRef.visit(this, scope);
		for (Expression e: expr.argList)
			e.visit(this, scope);
		return null;
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, ScopeStack scope) {
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, ScopeStack scope) {
		expr.classtype.visit(this, scope);
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, ScopeStack scope) {
		expr.eltType.visit(this, scope);
		expr.sizeExpr.visit(this, scope);
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, ScopeStack scope) {
		// Don't allow 'this' in static scopes
		if (scope.inStatic()) throw new StaticThisException(ref);
		
		ref.setDecl(scope.getCurrentClass());
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, ScopeStack scope) {
		ref.setDecl(scope.lookup(ref.id));
		return null;
	}

	@Override
	public Object visitIxIdRef(IxIdRef ref, ScopeStack scope) {
		ref.setDecl(scope.lookup(ref.id));
		ref.indexExpr.visit(this, scope);
		return null;
	}

	@Override
	public Object visitQRef(QRef ref, ScopeStack scope) {
		ref.ref.visit(this, scope); // Bottom up
		
		if (ref.ref.getDecl() == null) throw new UndefinedReferenceException(ref.ref);
		MemberDecl member = ref.ref.getDecl().getMember(ref.id);
		ref.setDecl(member);
		
		if (member.isPrivate && scope.getCurrentClass() != ref.ref.getDecl()) {
			throw new NotVisibleException(ref.ref, member);
		}
		
		return null;
	}

	@Override
	public Object visitIxQRef(IxQRef ref, ScopeStack scope) {
		ref.ref.visit(this, scope); // Bottom up
		
		if (ref.ref.getDecl() == null) throw new UndefinedReferenceException(ref.ref);
		MemberDecl member = ref.ref.getDecl().getMember(ref.id);
		ref.setDecl(member);
		
		if (member.isPrivate && scope.getCurrentClass() != ref.ref.getDecl()) {
			throw new NotVisibleException(ref.ref, member);
		}
		
		ref.ixExpr.visit(this, scope);
		return null;
	}

	@Override
	public Object visitIdentifier(Identifier id, ScopeStack scope) {
		throw new RuntimeException("Why am I visiting an Identifier?");
	}

	@Override
	public Object visitOperator(Operator op, ScopeStack scope) {
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, ScopeStack scope) {
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, ScopeStack scope) {
		return null;
	}

	@Override
	public Object visitNullLiteral(NullLiteral nlit, ScopeStack scope) {
		return null;
	}

}
