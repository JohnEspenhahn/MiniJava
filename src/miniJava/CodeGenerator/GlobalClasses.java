package miniJava.CodeGenerator;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Prim;
import mJAM.Machine.Reg;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.FieldDeclList;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodDeclList;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.ParameterDeclList;
import miniJava.AbstractSyntaxTrees.StatementList;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.ContextualAnalyzer.ScopeStack;
import miniJava.ContextualAnalyzer.Type;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class GlobalClasses {
	public static ClassDecl STRING_DECL;
	public static ClassType CLASSTYPE_STRING;
	public static Type TYPE_STRING;
	
	public static MethodDecl STRING_CHARAT_DECL;
	public static MethodDecl STRING_LENGTH_DECL;
	public static MethodDecl STRING_EQUALS_DECL;
	
	public static MethodDecl PRINTLN_DECL;
	
	static {
		CLASSTYPE_STRING = new ClassType(new Identifier(new Token(TokenKind.IDENTIFIER, "String")));
		MethodDeclList StringMethods = new MethodDeclList();
		
		// String.charAt(int)
		ParameterDeclList CharAtParams = new ParameterDeclList();
		CharAtParams.add(new ParameterDecl(new BaseType(TypeKind.INT, null), "n"));
		STRING_CHARAT_DECL = new MethodDecl(
				new FieldDecl(false, false, new BaseType(TypeKind.INT, null), "charAt"),
				CharAtParams, new StatementList());
		StringMethods.add(STRING_CHARAT_DECL);
		
		// String.length()
		STRING_LENGTH_DECL = new MethodDecl(
				new FieldDecl(false, false, new BaseType(TypeKind.INT, null), "length"),
				new ParameterDeclList(), new StatementList());
		StringMethods.add(STRING_LENGTH_DECL);
		
		// String.equals()
		ParameterDeclList EqualsParams = new ParameterDeclList();
		EqualsParams.add(new ParameterDecl(CLASSTYPE_STRING, "n"));
		STRING_EQUALS_DECL = new MethodDecl(
				new FieldDecl(false, false, new BaseType(TypeKind.BOOLEAN, null), "equals"),
				EqualsParams, new StatementList());
		StringMethods.add(STRING_EQUALS_DECL);
		
		STRING_DECL = new ClassDecl("String", new FieldDeclList(), StringMethods);
		
		TYPE_STRING = new Type(TypeKind.CLASS, GlobalClasses.STRING_DECL);
		CLASSTYPE_STRING.setDecl(STRING_DECL);
	}
	
	public static void define(ScopeStack stack) {
		// class System
		FieldDeclList SystemFields = new FieldDeclList();
		FieldDecl FSystemOut = new FieldDecl(false, true, 
				new ClassType(new Identifier(new Token(TokenKind.IDENTIFIER, "_PrintStream", null, null)), null), 
				"out", null);
		SystemFields.add(FSystemOut);
		stack.declare(new ClassDecl("System", SystemFields, new MethodDeclList()));
		
		// class _PrintStream
		MethodDeclList PrintStreamMethods = new MethodDeclList();
		ParameterDeclList PrintLnParams = new ParameterDeclList();
		PrintLnParams.add(new ParameterDecl(new BaseType(TypeKind.INT, null), "n"));
		PRINTLN_DECL = new MethodDecl(
				new FieldDecl(false, false, new BaseType(TypeKind.VOID, null), "println"),
				PrintLnParams, new StatementList());
		PrintStreamMethods.add(PRINTLN_DECL);
		ClassDecl PrintStream = new ClassDecl("_PrintStream", new FieldDeclList(), PrintStreamMethods);
		stack.declare(PrintStream);
		
		// class String
		stack.declare(STRING_DECL);
		
		// Visit types
		((ClassType) FSystemOut.type).setDecl(PrintStream);
	}
	
	private static int LB_BASE = 3;
	
	public static int defineCopyToHeap() {
		int copyToHeap = Machine.nextInstrAddr();
		
		Machine.emit(Op.LOAD, Reg.LB, -1);
		Machine.emit(Prim.arraylen);
		Machine.emit(Op.LOADL, 1);
		Machine.emit(Prim.sub); // End of array
		int while1 = Machine.nextInstrAddr();
		Machine.emit(Op.JUMP, Reg.CB, -1); // jump to condition
		int while1_body = Machine.nextInstrAddr();
		Machine.emit(Op.LOAD, Reg.LB, -1); // array
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE); // index
		Machine.emit(Op.LOADA, Reg.LB, -2); // load LB
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE); // i
		Machine.emit(Prim.sub);
		Machine.emit(Op.LOADI); // load value from stack
		Machine.emit(Prim.arrayupd); // copy
		Machine.emit(Op.LOADL, 1);
		Machine.emit(Prim.sub); // decrement i
		Machine.patch(while1, Machine.nextInstrAddr());
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE);
		Machine.emit(Op.LOADL, 0);
		Machine.emit(Prim.ge);
		Machine.emit(Op.JUMPIF, 1, Reg.CB, while1_body);
		Machine.emit(Op.RETURN, 0, 0, 0);
		
		return copyToHeap;
	}

	public static void defineStringEq() {
		int stringEq = Machine.nextInstrAddr();
		
		Machine.emit(Op.LOADA, Reg.OB, 0);
		Machine.emit(Prim.arraylen);
		Machine.emit(Op.LOADL, 1);
		Machine.emit(Prim.sub); // End of this array
		Machine.emit(Op.LOAD, Reg.LB, -1);
		Machine.emit(Prim.arraylen);
		Machine.emit(Op.LOADL, 1);
		Machine.emit(Prim.sub); // End of param array
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE);
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE+1);
		Machine.emit(Prim.ne);
		int if1 = Machine.nextInstrAddr();
		Machine.emit(Op.JUMPIF, 0, Reg.CB, -1); // -> L14
		Machine.emit(Op.LOADL, 0);
		Machine.emit(Op.RETURN, 1, 0, 1); // If length ne, return
		int while1 = Machine.nextInstrAddr();
		Machine.patch(if1, while1);
		Machine.emit(Op.JUMP, Reg.CB, -1); // L14 -> L17
		int while_body = Machine.nextInstrAddr();
		Machine.emit(Op.LOADA, Reg.OB, 0); // L15
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE);
		Machine.emit(Prim.arrayref);
		Machine.emit(Op.LOAD, Reg.LB, -1);
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE+1);
		Machine.emit(Prim.arrayref);
		Machine.emit(Prim.ne);
		int if2 = Machine.nextInstrAddr();
		Machine.emit(Op.JUMPIF, 0, Reg.CB, -1); // -> L16
		Machine.emit(Op.LOADL, 0);
		Machine.emit(Op.RETURN, 1, 0, 1); // If value ne, return
		Machine.patch(if2, Machine.nextInstrAddr());
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE); // L16
		Machine.emit(Op.LOADL, 1);
		Machine.emit(Prim.sub);
		Machine.emit(Op.STORE, Reg.LB, LB_BASE);
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE+1);
		Machine.emit(Op.LOADL, 1);
		Machine.emit(Prim.sub);
		Machine.emit(Op.STORE, Reg.LB, LB_BASE+1); // Decrement 
		Machine.patch(while1, Machine.nextInstrAddr());
		Machine.emit(Op.LOAD, Reg.LB, LB_BASE); // L17 (while condition)
		Machine.emit(Op.LOADL, 0);
		Machine.emit(Prim.ge);
		Machine.emit(Op.JUMPIF, 1, Reg.CB, while_body);
		Machine.emit(Op.LOADL, 1);
		Machine.emit(Op.RETURN, 1, 0, 1);
		
		GlobalClasses.STRING_EQUALS_DECL.setAddr(stringEq);		
	}
}
