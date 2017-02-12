package miniJava.SyntacticAnalyzer;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.LiteralExpr;

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
	public void testParseClassDec() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseDeclare() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseMethodContent() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseType() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParsePrimativeType() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseIdType() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseParamList() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseArgList() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseReference() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseReferenceExtension() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseIndexing() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseStatement() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseReferenceStatement() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseAssign() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseInvoke() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseExpression() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseDisjunction() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseConjunction() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseEquality() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseRelational() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseAdditive() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseMultiplicative() throws SyntaxError {
		fail("Not yet implemented");
	}

	@Test
	public void testParseValue() throws SyntaxError {
		astCheck(makeParser("123").parseValue(), new LiteralExpr(new IntLiteral(new Token(TokenKind.NUM, "123"))));
	}
	
	private Parser makeParser(String in) {
		return new Parser(new Scanner(new SourceStream(new ByteArrayInputStream(in.getBytes()))));
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
