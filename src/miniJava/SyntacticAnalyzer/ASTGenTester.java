package miniJava.SyntacticAnalyzer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.AbstractSyntaxTrees.ArrayType;
import miniJava.AbstractSyntaxTrees.AssignStmt;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BinaryExpr;
import miniJava.AbstractSyntaxTrees.BooleanLiteral;
import miniJava.AbstractSyntaxTrees.CallStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.ExprList;
import miniJava.AbstractSyntaxTrees.Expression;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.FieldDeclList;
import miniJava.AbstractSyntaxTrees.IdRef;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.IxIdRef;
import miniJava.AbstractSyntaxTrees.IxQRef;
import miniJava.AbstractSyntaxTrees.LiteralExpr;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodDeclList;
import miniJava.AbstractSyntaxTrees.NewArrayExpr;
import miniJava.AbstractSyntaxTrees.NewObjectExpr;
import miniJava.AbstractSyntaxTrees.Operator;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.ParameterDeclList;
import miniJava.AbstractSyntaxTrees.QRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.StatementList;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;

public class ASTGenTester {
	private static final ASTDisplay displayer = new ASTDisplay();
	private ByteArrayOutputStream out;
	
	@Before
	public void init() {
		Token.ALLOW_DEBUG = true;
		out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
	}
	
	@After
	public void cleanup() {
		Token.ALLOW_DEBUG = false;
		System.setOut(System.out);
	}
	
	@Test
	public void testParseClassDec() throws SyntaxError, FileNotFoundException {
		astCheck(makeParser(new File("tests/class_test1.mjava")).parseClassDec(),
				new ClassDecl("C1",
						fieldList(
								new FieldDecl(false, true, new ArrayType(new ClassType(id("String"))), "args"),
								new FieldDecl(true, false, new BaseType(TypeKind.INT), "p1")
								),
						methodList(
								new MethodDecl(
										new FieldDecl(false, true, new BaseType(TypeKind.VOID), "main"),
										paramList(new ParameterDecl(new ArrayType(new ClassType(id("String"))), "args")),
										statementList()
									),
								new MethodDecl(
										new FieldDecl(true, false, new ClassType(id("String")), "f1"),
										paramList(), statementList()
								),
								new MethodDecl(
										new FieldDecl(true, true, new ArrayType(new ClassType(id("String"))), "f2"),
										paramList(new ParameterDecl(new BaseType(TypeKind.BOOLEAN), "b")), statementList()
								)
						)
				));
	}

	@Test
	public void testParseDeclare() throws SyntaxError {
		astCheck(makeParser("public static String[] args;").parseDeclare(),
				new FieldDecl(false, true, new ArrayType(new ClassType(id("String"))), "args"));
		
		astCheck(makeParser("public static void main(String[] args) {}").parseDeclare(),
				new MethodDecl(
					new FieldDecl(false, true, new BaseType(TypeKind.VOID), "main"),
					paramList(new ParameterDecl(new ArrayType(new ClassType(id("String"))), "args")),
					statementList()
				));
		
		astCheck(makeParser("public static void main(String[] args, int k) {int i=0;int[] i =0;}").parseDeclare(),
				new MethodDecl(
					new FieldDecl(false, true, new BaseType(TypeKind.VOID), "main"),
					paramList(
							new ParameterDecl(new ArrayType(new ClassType(id("String"))), "args"),
							new ParameterDecl(new BaseType(TypeKind.INT), "k")
							),
					statementList(
							new VarDeclStmt(new VarDecl(new BaseType(TypeKind.INT), "i"), lit(0)),
							new VarDeclStmt(new VarDecl(new ArrayType(new BaseType(TypeKind.INT)), "i"), lit(0))
							)
				));
	}
	
	@Test
	public void testParseType() throws SyntaxError {
		astCheck(makeParser("int").parseType(),
				new BaseType(TypeKind.INT));
		
		astCheck(makeParser("int[]").parseType(),
				new ArrayType(new BaseType(TypeKind.INT)));
		
		astCheck(makeParser("boolean").parseType(),
				new BaseType(TypeKind.BOOLEAN));
		
		astCheck(makeParser("String").parseType(),
				new ClassType(id("String")));
		
		astCheck(makeParser("String[]").parseType(),
				new ArrayType(new ClassType(id("String"))));
	}

	@Test
	public void testParsePrimativeType() throws SyntaxError {
		astCheck(makeParser("int").parsePrimativeType(),
				new BaseType(TypeKind.INT));
		
		astCheck(makeParser("int[]").parsePrimativeType(),
				new ArrayType(new BaseType(TypeKind.INT)));
		
		astCheck(makeParser("boolean").parsePrimativeType(),
				new BaseType(TypeKind.BOOLEAN));
	}

	@Test
	public void testParseIdType() throws SyntaxError {
		astCheck(makeParser("String").parseIdType(),
				new ClassType(id("String")));
		
		astCheck(makeParser("String[]").parseIdType(),
				new ArrayType(new ClassType(id("String"))));
	}

	@Test
	public void testParseParamList() throws SyntaxError {
		astCheck(makeParser("String s, int a, boolean b, Long l").parseParamList(),
				paramList(
						new ParameterDecl(new ClassType(id("String")), "s"),
						new ParameterDecl(new BaseType(TypeKind.INT), "a"),
						new ParameterDecl(new BaseType(TypeKind.BOOLEAN), "b"),
						new ParameterDecl(new ClassType(id("Long")), "l")));
	}

	@Test
	public void testParseStatement() throws SyntaxError {
		astCheck(makeParser("int i = 0;").parseStatement(), 
				new VarDeclStmt(new VarDecl(new BaseType(TypeKind.INT), "i"), lit(0)));
		
		astCheck(makeParser("int[] i = 0;").parseStatement(), 
				new VarDeclStmt(new VarDecl(new ArrayType(new BaseType(TypeKind.INT)), "i"), lit(0)));
		
		astCheck(makeParser("String i = 0;").parseStatement(), 
				new VarDeclStmt(new VarDecl(new ClassType(id("String")), "i"), lit(0)));
		
		astCheck(makeParser("String[] i = 0;").parseStatement(), 
				new VarDeclStmt(new VarDecl(new ArrayType(new ClassType(id("String"))), "i"), lit(0)));
		
		astCheck(makeParser("String[] i = a;").parseStatement(), 
				new VarDeclStmt(new VarDecl(new ArrayType(new ClassType(id("String"))), "i"), new RefExpr(new IdRef(id("a")))));
		
		astCheck(makeParser("i = 0;").parseStatement(), 
				new AssignStmt(new IdRef(id("i")), lit(0)));
		
		astCheck(makeParser("i.j = 0;").parseStatement(), 
				new AssignStmt(new QRef(new IdRef(id("i")), id("j")), lit(0)));
		
		astCheck(makeParser("i.j.k = 0;").parseStatement(), 
				new AssignStmt(new QRef(new QRef(new IdRef(id("i")), id("j")), id("k")), lit(0)));
		
		astCheck(makeParser("i[1].j = 0;").parseStatement(), 
				new AssignStmt(new QRef(new IxIdRef(id("i"), lit(1)), id("j")), lit(0)));
		
		astCheck(makeParser("i.j[2] = 0;").parseStatement(), 
				new AssignStmt(new IxQRef(new IdRef(id("i")), id("j"), lit(2)), lit(0)));
		
		astCheck(makeParser("i.j[2].k = 0;").parseStatement(), 
				new AssignStmt(new QRef(new IxQRef(new IdRef(id("i")), id("j"), lit(2)), id("k")), lit(0)));
		
		astCheck(makeParser("i.j[2].k[3] = 0;").parseStatement(), 
				new AssignStmt(new IxQRef(new IxQRef(new IdRef(id("i")), id("j"), lit(2)), id("k"), lit(3)), lit(0)));
		
		astCheck(makeParser("this = 0;").parseStatement(),
				new AssignStmt(new ThisRef(), lit(0)));
		
		astCheck(makeParser("C c = new C();").parseStatement(),
				new VarDeclStmt(new VarDecl(new ClassType(id("C")), "c"), new NewObjectExpr(new ClassType(id("C")))));
		
		astCheck(makeParser("this.x = 0;").parseStatement(),
				new AssignStmt(new QRef(new ThisRef(), id("x")), lit(0)));
				
		astCheck(makeParser("this.x[1] = 0;").parseStatement(),
				new AssignStmt(new IxQRef(new ThisRef(), id("x"), lit(1)), lit(0)));
		
		astCheck(makeParser("this();").parseStatement(),
				new CallStmt(new ThisRef(), new ExprList()));
		
		astCheck(makeParser("a(1, 2);").parseStatement(),
				new CallStmt(new IdRef(id("a")), exprList(lit(1), lit(2))));
		
		astCheck(makeParser("a.b(1, 2);").parseStatement(),
				new CallStmt(new QRef(new IdRef(id("a")), id("b")), exprList(lit(1), lit(2))));
		
		astCheck(makeParser("this.b(1, 2);").parseStatement(),
				new CallStmt(new QRef(new ThisRef(), id("b")), exprList(lit(1), lit(2))));
		
		astCheck(makeParser("a.c[b](1, 2);").parseStatement(),
				new CallStmt(new IxQRef(new IdRef(id("a")), id("c"), new RefExpr(new IdRef(id("b")))), exprList(lit(1), lit(2))));
		
		astCheck(makeParser("a[1](1, 2);").parseStatement(),
				new CallStmt(new IxIdRef(id("a"), lit(1)), exprList(lit(1), lit(2))));
		
		astCheck(makeParser("this.that.those[3] = them;").parseStatement(),
				new AssignStmt(new IxQRef(new QRef(new ThisRef(), id("that")), id("those"), lit(3)), new RefExpr(new IdRef(id("them")))));
	}

	@Test
	public void testParseExpression() throws SyntaxError {
		astCheck(makeParser("1+2/2").parseExpression(),
				new BinaryExpr(plus(), lit(1), new BinaryExpr(div(), lit(2), lit(2))));
	}

	@Test
	public void testParseDisjunction() throws SyntaxError {
		astCheck(makeParser("false && true == 2/1 || 1>5").parseExpression(),
				new BinaryExpr(or(),
					new BinaryExpr(and(),
						lit(false),
						new BinaryExpr(equ(),lit(true),new BinaryExpr(div(), lit(2), lit(1)))),
					new BinaryExpr(gtr(), lit(1), lit(5))));
		
		astCheck(makeParser("1 > 5 || false && true == 2/1").parseExpression(),
				new BinaryExpr(or(),
					new BinaryExpr(gtr(), lit(1), lit(5)),
					new BinaryExpr(and(),
						lit(false),
						new BinaryExpr(equ(),lit(true),new BinaryExpr(div(), lit(2), lit(1))))));
	}

	@Test
	public void testParseConjunction() throws SyntaxError {
		astCheck(makeParser("false && true == 2/1").parseExpression(),
				new BinaryExpr(and(),
					lit(false),
					new BinaryExpr(equ(),lit(true),new BinaryExpr(div(), lit(2), lit(1)))));
	}

	@Test
	public void testParseEquality() throws SyntaxError {
		astCheck(makeParser("2/1 > 3+4 == true && false").parseExpression(),
				new BinaryExpr(and(),
					new BinaryExpr(equ(),
						new BinaryExpr(gtr(),
							new BinaryExpr(div(), lit(2), lit(1)),
							new BinaryExpr(plus(), lit(3), lit(4))),
						lit(true)),
				lit(false)));
		
		astCheck(makeParser("false && 2/1 > 3+4 == true").parseExpression(),
				new BinaryExpr(and(),
					lit(false),
					new BinaryExpr(equ(),
						new BinaryExpr(gtr(),
							new BinaryExpr(div(), lit(2), lit(1)),
							new BinaryExpr(plus(), lit(3), lit(4))),
						lit(true))));
	}

	@Test
	public void testParseRelational() throws SyntaxError {
		astCheck(makeParser("2/1 > 3+4").parseExpression(),
				new BinaryExpr(gtr(),
					new BinaryExpr(div(), lit(2), lit(1)),
					new BinaryExpr(plus(), lit(3), lit(4))));
		
		astCheck(makeParser("2/1 > 3+4 == true").parseExpression(),
				new BinaryExpr(equ(),
					new BinaryExpr(gtr(),
						new BinaryExpr(div(), lit(2), lit(1)),
						new BinaryExpr(plus(), lit(3), lit(4))),
				lit(true)));
	}

	@Test
	public void testParseAdditive() throws SyntaxError {
		astCheck(makeParser("2/-(1+3)+4").parseExpression(),
				new BinaryExpr(plus(), 
						new BinaryExpr(div(), lit(2), 
								new UnaryExpr(minus(), 
										new BinaryExpr(plus(), lit(1), lit(3)))),
						lit(4)));
		
		astCheck(makeParser("2/-1+3+4").parseExpression(),
				new BinaryExpr(plus(),
					new BinaryExpr(plus(),
							new BinaryExpr(div(), lit(2), new UnaryExpr(minus(), lit(1))),
							lit(3)),
					lit(4)));
						
		
		astCheck(makeParser("4+2/-(1+3)").parseExpression(),
				new BinaryExpr(plus(), lit(4),
						new BinaryExpr(div(), lit(2), 
								new UnaryExpr(minus(), new BinaryExpr(plus(), lit(1), lit(3))))));
		
		astCheck(makeParser("2/-(1+3+4)").parseExpression(),
				new BinaryExpr(div(), lit(2), 
						new UnaryExpr(minus(),
								new BinaryExpr(plus(), 
										new BinaryExpr(plus(), lit(1), lit(3)), 
										lit(4)))));
	}

	@Test
	public void testParseMultiplicative() throws SyntaxError {
		astCheck(makeParser("2/-2").parseExpression(),
				new BinaryExpr(div(), lit(2), new UnaryExpr(minus(), lit(2))));
		
		astCheck(makeParser("2/-(1+3)").parseExpression(),
				new BinaryExpr(div(), lit(2), 
						new UnaryExpr(minus(), 
								new BinaryExpr(plus(), lit(1), lit(3)))));
	}

	@Test
	public void testParseValue() throws SyntaxError {
		astCheck(makeParser("123").parseValue(), 
				new LiteralExpr(new IntLiteral(new Token(TokenKind.NUM, "123"))));
		
		astCheck(makeParser("new String()").parseValue(),
				new NewObjectExpr(new ClassType(id("String"))));
		
		astCheck(makeParser("new int[5]").parseValue(),
				new NewArrayExpr(new ArrayType(new BaseType(TypeKind.INT)), lit(5)));
		
		astCheck(makeParser("new String[5]").parseValue(),
				new NewArrayExpr(new ArrayType(new ClassType(id("String"))), lit(5)));
	}
	
	private Parser makeParser(File f) throws FileNotFoundException {
		return new Parser(new Scanner(new SourceStream(new FileInputStream(f))));
	}
	
	private Parser makeParser(String in) {
		return new Parser(new Scanner(new SourceStream(new ByteArrayInputStream(in.getBytes()))));
	}
	
	private static ExprList exprList(Expression... exprs) {
		ExprList list = new ExprList();
		for (Expression e: exprs) list.add(e);
		return list;
	}
	
	private static ParameterDeclList paramList(ParameterDecl... decl) {
		ParameterDeclList list = new ParameterDeclList();
		for (ParameterDecl d: decl) list.add(d);
		return list;
	}
	
	private static StatementList statementList(Statement... stmt) {
		StatementList list = new StatementList();
		for (Statement s: stmt) list.add(s);
		return list;
	}
	
	private static FieldDeclList fieldList(FieldDecl... fields) {
		FieldDeclList list = new FieldDeclList();
		for (FieldDecl f: fields) list.add(f);
		return list;
	}
	
	private static MethodDeclList methodList(MethodDecl... methods) {
		MethodDeclList list = new MethodDeclList();
		for (MethodDecl m: methods) list.add(m);
		return list;
	}
	
	private static Operator or() {
		return new Operator(new Token(TokenKind.OR, "||"));
	}
	
	private static Operator and() {
		return new Operator(new Token(TokenKind.AND, "&&"));
	}
	
	private static Operator equ() {
		return new Operator(new Token(TokenKind.EQU, "=="));
	}
	
	private static Operator gtr() {
		return new Operator(new Token(TokenKind.GTR, ">"));
	}
	
	private static Operator plus() {
		return new Operator(new Token(TokenKind.PLUS, "+"));
	}
	
	private static Operator minus() {
		return new Operator(new Token(TokenKind.MINUS, "-"));
	}
	
	private static Operator div() {
		return new Operator(new Token(TokenKind.DIV, "/"));
	}
	
	private static Expression lit(int i) {
		return new LiteralExpr(new IntLiteral(new Token(TokenKind.NUM, "" + i)));
	}
	
	private static Expression lit(boolean b) {
		return new LiteralExpr(new BooleanLiteral(new Token(TokenKind.BOOLEAN, "" + b)));
	}
	
	private static Identifier id(String name) {
		return new Identifier(new Token(TokenKind.IDENTIFIER, name));
	}
	
	@SuppressWarnings("rawtypes")
	private void astCheck(Iterable iable1, Iterable iable2) {
		Iterator ia1 = iable1.iterator(),
				 ia2 = iable2.iterator();
		
		while (ia1.hasNext() && ia2.hasNext()) {
			astCheck((AST) ia1.next(), (AST) ia2.next());
		}
		
		org.junit.Assert.assertEquals(ia1.hasNext(), false);
		org.junit.Assert.assertEquals(ia2.hasNext(), false);
	}
	
	private void astCheck(AST a1, AST a2) {
		out.reset();
		displayer.showTree(a1);
		String a1_str = out.toString();
		
		out.reset();
		displayer.showTree(a2);
		String a2_str = out.toString();
		
		org.junit.Assert.assertEquals(a1_str, a2_str);
	}

}
