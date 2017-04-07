package miniJava.CodeGenerator;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Prim;
import mJAM.Machine.Reg;
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
import miniJava.AbstractSyntaxTrees.Expression;
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
import miniJava.AbstractSyntaxTrees.ThisDecl;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;
import miniJava.CodeGenerator.RuntimeDescription.AbsoluteAddress;
import miniJava.CodeGenerator.RuntimeDescription.RelativeAddress;
import miniJava.CodeGenerator.RuntimeDescription.RuntimeDescription;
import miniJava.ContextualAnalyzer.ScopeStack;
import miniJava.SyntacticAnalyzer.TokenKind;

public class CodeGenVisitor implements Visitor<Object, Object> {
	
	private Frame frame;

	public void visit(Package prog) {
		prog.visit(this, null);
	}
	
	@Override
	public Object visitPackage(Package prog, Object arg) {		
		Machine.initCodeGen();
		
		// Visit all fields
		int staticAddrs = 0;
		for (ClassDecl cd: prog.classDeclList) {
			int offset = 0;
			for (int i = 0; i < cd.fieldDeclList.size(); i++) {
				FieldDecl fd = cd.fieldDeclList.get(i);
				if (fd.isStatic) {
					fd.setAbsoluteAddress(staticAddrs);
					staticAddrs += 1;
				} else {
					fd.setObjectOffset(offset);
					offset += 1;
				}
			}
			cd.instanceSize = offset+1;
		}
		
		if (staticAddrs > 0)
			Machine.emit(Op.PUSH, staticAddrs); // static fields
		
		Machine.emit(Op.LOADL, 0); // arr length
		Machine.emit(Prim.newarr);

		Machine.emit(Op.CALL,Reg.CB,-1);     // static call main (address to be patched)
		prog.main.patch(Machine.nextInstrAddr()-1);
		
		Machine.emit(Op.HALT,0,0,0);         // end execution
		
		// Visit classes
		for (ClassDecl cd: prog.classDeclList)
			cd.visit(this, null);
		
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg) {
		// Already visited fields
		
		for (MethodDecl md: cd.methodDeclList)
			md.visit(this, null);
		
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg) {
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg) {
		frame = new Frame(md.parameterDeclList.size());
		
		for (int i = 0, len = md.parameterDeclList.size(); i < len; i++) {
			ParameterDecl pd = md.parameterDeclList.get(i);
			pd.setLocalOffset(-len+i); // (-len...-1)
		}
		
		md.setAddr(Machine.nextInstrAddr());		
		for (Statement s: md.statementList) {
			s.visit(this, null);
		}
		
		frame = null;
		
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg) {
		// Offset saved when visiting method decl
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg) {
		decl.setLocalOffset(frame.getLocalBase());
		return null;
	}

	@Override
	public Object visitBaseType(BaseType type, Object arg) {
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg) {
		return null;
	}

	@Override
	public Object visitArrayType(ArrayType type, Object arg) {
		return null;
	}

	@Override
	public Object visitBlockStmt(BlockStmt stmt, Object arg) {
		for (Statement s: stmt.sl) {
			s.visit(this, null);
		}
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg) {
		stmt.varDecl.visit(this, null);
		stmt.initExp.visit(this, null); // Result will be in correct spot
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg) {
		RuntimeDescription rd = stmt.ref.getRuntimeDesc();
		if (rd instanceof AbsoluteAddress) {
			stmt.val.visit(this, null);
			rd.store();
		} else if (stmt.ref instanceof QRef) {
			// load address of owning object
			((QRef) stmt.ref).ref.visit(this, null);
			// load index of field
			Machine.emit(Op.LOADL, ((RelativeAddress) rd).getOffset());
			// load val
			stmt.val.visit(this, null);
			// fieldupd
			Machine.emit(Prim.fieldupd);
		} else {
			stmt.val.visit(this, null);
			rd.store();
		}
		
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg) {
		// Load parameters to stack
		for (Expression exp: stmt.argList)
			exp.visit(this, null);
		
		// Check special cases
		if (stmt.methodRef.getDecl() == ScopeStack.PRINTLN_DECL) {
			Machine.emit(Prim.putintnl);
			return null;
		}
		
		MethodDecl md = (MethodDecl) stmt.methodRef.getDecl();		
		if (md.isStatic) {
			// Don't care about object
			Machine.emit(Op.CALL, Reg.CB, -1);
			md.patch(Machine.nextInstrAddr()-1);
		} else {
			if (stmt.methodRef instanceof QRef) {
				// Load address of owning object
				((QRef) stmt.methodRef).ref.visit(this, null);
			} else {
				// Current object is owning object
				Machine.emit(Op.LOADA, Reg.OB, 0);
			}
			
			// Call the method on this object
			Machine.emit(Op.CALLI, Reg.CB, -1);
			md.patch(Machine.nextInstrAddr()-1);
		}
		
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg) {
		if (stmt.returnExpr != null) {
			stmt.returnExpr.visit(this, null);
			Machine.emit(Op.RETURN, 1, 0, frame.getNumArgs());
		} else {
			Machine.emit(Op.RETURN, 0, 0, frame.getNumArgs());
		}
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg) {
		// Evaluate condition
		stmt.cond.visit(this, null);
		
		// Jump to else if condition false
		int cond = Machine.nextInstrAddr();
		Machine.emit(Op.JUMPIF, 0, Reg.CB, -1);
		
		// Body
		stmt.thenStmt.visit(this, null);

		if (stmt.elseStmt != null) {
			// Skip else after body
			int jumpEnd = Machine.nextInstrAddr();
			Machine.emit(Op.JUMP, Reg.CB, -1);
			
			// If condition false, jump to else
			int elseBody = Machine.nextInstrAddr();
			Machine.patch(cond, elseBody);
			
			// Else
			stmt.elseStmt.visit(this, null);
			
			// Patch "skip else after body"
			int end = Machine.nextInstrAddr();
			Machine.patch(jumpEnd, end);
		} else {
			// If no else, false condition jump to end
			int end = Machine.nextInstrAddr();
			Machine.patch(cond, end);
		}
		
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg) {
		// Jump to condition
		int condJump = Machine.nextInstrAddr();
		Machine.emit(Op.JUMP, Reg.CB, -1);
		
		// Evaluate body
		int body = Machine.nextInstrAddr();
		stmt.body.visit(this, null);
		
		// Evaluate condition
		int cond = Machine.nextInstrAddr();
		stmt.cond.visit(this, null);
		Machine.patch(condJump, cond);
		
		// Jump back to body if true
		Machine.emit(Op.JUMPIF, 1, Reg.CB, body);
		
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg) {
		switch (expr.operator.kind) {
		case NOT:
			expr.expr.visit(this, null);
			Machine.emit(Prim.not);
			break;
		case MINUS:
			// Special case for "-" "num"
			if (expr.expr instanceof LiteralExpr) {
				LiteralExpr lexp = (LiteralExpr) expr.expr;
				if (lexp.lit instanceof IntLiteral) {
					IntLiteral lit = (IntLiteral) lexp.lit;
					lit.spelling = "-" + lit.spelling;
					lit.visit(this, null);
					break;
				}
			}
			
			expr.expr.visit(this, null);
			Machine.emit(Prim.neg);
			break;
		default:
			throw new RuntimeException("Unsupported " + expr.operator.kind);
		}
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg) {
		expr.left.visit(this, null);
		expr.right.visit(this, null);
		expr.operator.visit(this, null);
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg) {
		expr.ref.visit(this, null);
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg) {
		expr.lit.visit(this, null);
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg) {		
		Machine.emit(Op.LOADL, -1);
		Machine.emit(Op.LOADL, expr.classtype.getDecl().instanceSize);
		Machine.emit(Prim.newobj);
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg) {
		Machine.emit(Op.LOADA, Reg.OB, 0);
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg) {
		// Load directly
		RuntimeDescription rd = ref.getRuntimeDesc();
		if (rd != null) rd.load(); // Might be null if can ignore (i.e. a class name)
		return null;
	}

	@Override
	public Object visitIxIdRef(IxIdRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitQRef(QRef ref, Object arg) {
		RuntimeDescription rd = ref.getRuntimeDesc();
		
		if (rd instanceof AbsoluteAddress) {
			// Load directly
			rd.load();
		} else {
			// Get object we are loading on
			ref.ref.visit(this, null);
			// load index of field
			Machine.emit(Op.LOADL, ((RelativeAddress) rd).getOffset());
			// Load field's value
			Machine.emit(Prim.fieldref);
		}

		return null;
	}

	@Override
	public Object visitIxQRef(IxQRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdentifier(Identifier id, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitOperator(Operator op, Object arg) {
		switch (op.kind) {
		case PLUS:
			Machine.emit(Prim.add);
			break;
		case MINUS:
			Machine.emit(Prim.sub);
			break;
		case MULT:
			Machine.emit(Prim.mult);
			break;
		case DIV:
			Machine.emit(Prim.div);
			break;
		case AND:
			Machine.emit(Prim.add);
			break;
		case OR:
			Machine.emit(Prim.or);
			break;
		case GTR:
			Machine.emit(Prim.gt);
			break;
		case LSS:
			Machine.emit(Prim.lt);
			break;
		case GTR_EQU:
			Machine.emit(Prim.ge);
			break;
		case LSS_EQU:
			Machine.emit(Prim.le);
			break;
		case EQU:
			Machine.emit(Prim.eq);
			break;
		case NOT_EQU:
			Machine.emit(Prim.ne);
			break;
		default:
			throw new RuntimeException("Unsupported " + op.kind);
		}
		
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg) {
		Machine.emit(Op.LOADL, Integer.parseInt(num.spelling));
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		if (bool.kind == TokenKind.TRUE)
			Machine.emit(Op.LOADL, 1);
		else
			Machine.emit(Op.LOADL, 0);
		
		return null;
	}

	@Override
	public Object visitNullLiteral(NullLiteral nlit, Object arg) {
		Machine.emit(Op.LOADL, 0);
		return null;
	}

	@Override
	public Object visitThisDecl(ThisDecl decl, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitArrayIdxDecl(ArrayIdxDecl decl, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

}
