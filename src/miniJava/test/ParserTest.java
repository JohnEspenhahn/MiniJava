package miniJava.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.SourceStream;

public class ParserTest {

	@Test
	public void test() {
		// CLASS DECLARATION
		checkParse("class c { public static boolean b; }", WrapperType.NONE, true);
		// FIELD DECLARATION
		checkParse("public static int log_id_123_a;", WrapperType.CLASS, true);
		// METHOD DECLARATION
		checkParse("public void function1(int P1, String p2) { }", WrapperType.CLASS, true);
		// VISIBILITY
		checkParse("private void f() {}", WrapperType.CLASS, true);
		checkParse("public void f() { }", WrapperType.CLASS, true);
		// ACCESS
		checkParse("static void f() { }", WrapperType.CLASS, true);
		// TYPE
		checkParse("int i; boolean b; SomeOtherClass c; int[] ints; Another123[] objs;", WrapperType.CLASS, true);
		// PARAM LIST
		checkParse("void function(int id1, boolean id2, String id3) {}", WrapperType.CLASS, true);
		// REFERENCE
		checkParse("identifier[1+2-3/4*5]", WrapperType.RESULT, true);
		checkParse("this", WrapperType.RESULT, true);
		checkParse("ref.ref2[1<2]", WrapperType.CONDITIONAL, true);
		checkParse("this.c.k = 1;", WrapperType.FUNCTION, true);
		checkParse("this.c[b.d.f[1].a].k = 1;", WrapperType.FUNCTION, true);
		// STATEMENT
		checkParse("return \n\t 1 <= 2;", WrapperType.FUNCTION, true);
		checkParse("if (1 == 2) func(); else func2();", WrapperType.FUNCTION, true);
		checkParse("while (id1 && id2 || id3 && !id4 || id5 != id6) { }", WrapperType.FUNCTION, true);
		// EXPRESSION
		checkParse("return true; return false;", WrapperType.FUNCTION, true);
		checkParse("id myid = new id(); int[] ints = 1+!1;", WrapperType.FUNCTION, true);
		checkParse("(1 > 2)", WrapperType.CONDITIONAL, true);
		checkParse("int i = 0;", WrapperType.FUNCTION, true);
		checkParse("boolean b = i;", WrapperType.FUNCTION, true);
		// COMMENT
		checkParse("int i = 1; // sdlkf j;askdjf a;lksjfdlk sajdlkf\n", WrapperType.FUNCTION, true);
		checkParse("int j /* askdfj lasjfd lkasjfd kajsd;f */ = 2;", WrapperType.FUNCTION, true);
		
		// INVALID SCAN
		checkParse(":", WrapperType.FUNCTION, false);
		checkParse("return;", WrapperType.CLASS, false);
		checkParse("return", WrapperType.CONDITIONAL, false);
		checkParse("return", WrapperType.RESULT, false);
		checkParse("if (true) { print(100); }", WrapperType.CLASS, false);
		checkParse("if (true) { print(100); }", WrapperType.RESULT, false);
		checkParse("class { void f() { } }", WrapperType.CLASS, false);
		checkParse("if (true { }) { }", WrapperType.FUNCTION, false);
		checkParse("this.this = 1;", WrapperType.FUNCTION, false);
		checkParse("this.this.k = 1;", WrapperType.FUNCTION, false);
	}
	
	private void checkParse(String in, WrapperType wrap, boolean ok) {
		switch (wrap) {
		case NONE:
			break;
		case CLASS:
			in = "class WrapperClass { " + in + " }";
			break;
		case FUNCTION:
			in = "class WrapperFClass { public static void main(String[] args) {" + in + "} }";
			break;
		case CONDITIONAL:
			in = "class WrapperCClass { public static void main(String[] args) { if(" + in + ") ok(); else bad(); } }";
			break;
		case RESULT:
			in = "class WrapperRClass { public static void main(String[] args) { ok(" + in + "); } }";
			break;
		}
		
		Scanner s = new Scanner(new SourceStream(new ByteArrayInputStream(in.getBytes())));
		Parser p = new Parser(s);
		
		assertEquals(ok, p.parseProgram(false));
	}

	enum WrapperType {
		NONE, CLASS, FUNCTION, CONDITIONAL, RESULT
	}
}
