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
		checkParse("class c { } class c2 { }", WrapperType.NONE, true);
		checkParse("class c {} class", WrapperType.NONE, false);
		checkParse("class c }", WrapperType.NONE, false);
		checkParse("class c { int t^ = 1; }", WrapperType.NONE, false);
		// FIELD DECLARATION
		checkParse("public static int log_id_123_a;", WrapperType.CLASS, true);
		// METHOD DECLARATION
		checkParse("public void function1(int P1, String p2) { }", WrapperType.CLASS, true);
		checkParse("public f1() { }", WrapperType.CLASS, false);
		checkParse("public private f1() { }", WrapperType.CLASS, false);
		checkParse("f1() {}", WrapperType.CLASS, false);
		checkParse("static f1() {}", WrapperType.CLASS, false);
		checkParse("static private f1() {}", WrapperType.CLASS, false);
		checkParse("static private Type f1() {}", WrapperType.CLASS, false);
		checkParse("private static Type f1() {}", WrapperType.CLASS, true);
		checkParse("private static f1() {}", WrapperType.CLASS, false);
		checkParse("private static void f1() {}", WrapperType.CLASS, true);
		checkParse("private void static f1() {}", WrapperType.CLASS, false);
		checkParse("void private static f1() {}", WrapperType.CLASS, false);
		checkParse("void f1()", WrapperType.CLASS, false);
		checkParse("void f1 {}", WrapperType.CLASS, false);
		checkParse("Type f1()", WrapperType.CLASS, false);
		checkParse("Type f1 {}", WrapperType.CLASS, false);
		// VISIBILITY
		checkParse("private void f() {}", WrapperType.CLASS, true);
		checkParse("public void f() { }", WrapperType.CLASS, true);
		// ACCESS
		checkParse("static void f() { }", WrapperType.CLASS, true);
		// TYPE
		checkParse("int i; boolean b; SomeOtherClass c; int[] ints; Another123[] objs;", WrapperType.CLASS, true);
		checkParse("int i", WrapperType.CLASS, false);
		checkParse("int[] i", WrapperType.CLASS, false);
		checkParse("boolean i", WrapperType.CLASS, false);
		checkParse("Type i", WrapperType.CLASS, false);
		checkParse("int i;", WrapperType.CLASS, true);
		checkParse("int[] i;", WrapperType.CLASS, true);
		checkParse("boolean i;", WrapperType.CLASS, true);
		checkParse("Type i;", WrapperType.CLASS, true);
		// PARAM LIST
		checkParse("void function(int id1, boolean id2, String id3) {}", WrapperType.CLASS, true);
		checkParse("Type func(int a) {}", WrapperType.CLASS, true);
		checkParse("Type func(int a, int b) {}", WrapperType.CLASS, true);
		checkParse("Type func(int a.j, int b) {}", WrapperType.CLASS, false);
		checkParse("Type func(int a, j, int b) {}", WrapperType.CLASS, false);
		checkParse("Type func(int a, int b) {} {}", WrapperType.CLASS, false);
		checkParse("Type func(int a, int (b)) {}", WrapperType.CLASS, false);
		checkParse("Type func(int a, (int b)) {}", WrapperType.CLASS, false);
		checkParse("Type[] func(int a, int[] j, int b) {}", WrapperType.CLASS, true);
		// ARGLIST
		checkParse("f1(b, b[0], b.c, b.c.d, b.c[2].d, 1, 1/2, 2*3/1);", WrapperType.FUNCTION, true);
		checkParse("f1(b, b[0], b.c, b.c.d, b.c[2].d, 1, 1/2, 2*3/1)", WrapperType.FUNCTION, false);
		// REFERENCE
		checkParse("identifier[1+2-3/4*5]", WrapperType.RESULT, true);
		checkParse("this", WrapperType.RESULT, true);
		checkParse("1cd.b = 1;", WrapperType.FUNCTION, false);
		checkParse("_1cd.b = 1;", WrapperType.FUNCTION, false);
		checkParse("a_1cd.b = 1;", WrapperType.FUNCTION, true);
		checkParse("ref.ref2[1<2]", WrapperType.CONDITIONAL, true);
		checkParse("ref.ref2[1<2] = 1;", WrapperType.FUNCTION, true);
		checkParse("ref.ref2[1<2] = 1", WrapperType.FUNCTION, false);
		checkParse("ref.ref2[1<2] = bc_d1__d;", WrapperType.FUNCTION, true);
		checkParse("this.c.k = 1;", WrapperType.FUNCTION, true);
		checkParse("this.c.k = 1", WrapperType.FUNCTION, false);
		checkParse("this.c[b.d.f[1].a].k = 1;", WrapperType.FUNCTION, true);
		checkParse("this.c[b.d.f[1].a].k = 1", WrapperType.FUNCTION, false);
		// STATEMENT
		checkParse("return \n\t 1 <= 2;", WrapperType.FUNCTION, true);
		checkParse("if (1 == 2) func(); else func2();", WrapperType.FUNCTION, true);
		checkParse("if (1 == 2) func(); else func2()", WrapperType.FUNCTION, false);
		checkParse("if (1 == 2) func() else func2();", WrapperType.FUNCTION, false);
		checkParse("if (1 == 2) func();", WrapperType.FUNCTION, true);
		checkParse("if (1 == 2) { func(); }", WrapperType.FUNCTION, true);
		checkParse("if (1 == 2) { func(); } else { func2(); }", WrapperType.FUNCTION, true);
		checkParse("while (id1 && id2 || id3 && !id4 || id5 != id6) { }", WrapperType.FUNCTION, true);
		checkParse("while (id1 && id2 || id3 && !id4 || id5 != id6) { } else {}", WrapperType.FUNCTION, false);
		checkParse("while (id1 && id2 || id3 && !id4 || id5 != id6) { };", WrapperType.FUNCTION, false);
		checkParse("int i = 0;", WrapperType.FUNCTION, true);
		checkParse("int i j = 0;", WrapperType.FUNCTION, false);
		checkParse("int i = 0; i j = 0;", WrapperType.FUNCTION, true);
		
		// Statement Assign/Invoke
		checkParse("int i = i j = 0;", WrapperType.FUNCTION, false);
		checkParse("int i = i = 0;", WrapperType.FUNCTION, false);
		checkParse("int[] i = j;", WrapperType.FUNCTION, true);
		checkParse("Type[] i = j;", WrapperType.FUNCTION, true);
		checkParse("this i = j;", WrapperType.FUNCTION, false);
		checkParse("id.id2();", WrapperType.FUNCTION, true);
		checkParse("int.j = j;", WrapperType.FUNCTION, false);
		checkParse("this.j i = k;", WrapperType.FUNCTION, false);
		checkParse("this i.j = k;", WrapperType.FUNCTION, false);
		checkParse("this = 1;", WrapperType.FUNCTION, true);
		checkParse("this();", WrapperType.FUNCTION, true);
		checkParse("int();", WrapperType.FUNCTION, false);
		checkParse("int = 1;", WrapperType.FUNCTION, false);
		
		checkParse("int[] i = j[1];", WrapperType.FUNCTION, true);
		checkParse("int[] i = j[];", WrapperType.FUNCTION, false);
		checkParse("Type id = new Type();", WrapperType.FUNCTION, true);
		checkParse("id = new Type();", WrapperType.FUNCTION, true);
		checkParse("id();", WrapperType.FUNCTION, true);
		checkParse("this = 1", WrapperType.FUNCTION, false);
		checkParse("this()", WrapperType.FUNCTION, false);
		checkParse("Type id = new Type()", WrapperType.FUNCTION, false);
		checkParse("id = new Type()", WrapperType.FUNCTION, false);
		checkParse("id()", WrapperType.FUNCTION, false);
		checkParse("{}", WrapperType.FUNCTION, true);
		checkParse("{ int i = 0; }", WrapperType.FUNCTION, true);
		checkParse("{ int i = 0; int j = 1; }", WrapperType.FUNCTION, true);
		checkParse("{ int i = 0; int j = 1; int k = 2; }", WrapperType.FUNCTION, true);
		// EXPRESSION
		checkParse("return true; return false;", WrapperType.FUNCTION, true);
		checkParse("return true", WrapperType.FUNCTION, false);
		checkParse("id myid = new id(); int[] ints = 1+!1;", WrapperType.FUNCTION, true);
		checkParse("(1 > 2)", WrapperType.CONDITIONAL, true);
		checkParse("(1 > 2 < 3)", WrapperType.CONDITIONAL, true);
		checkParse("(1 > 2 < 3 == 4)", WrapperType.CONDITIONAL, true);
		checkParse("(1 > 2 < 3 == 4 + 5)", WrapperType.CONDITIONAL, true);
		checkParse("(1 > 2 < 3 == 4 ++ 5)", WrapperType.CONDITIONAL, false);
		checkParse("(1 > (2 < 3))", WrapperType.CONDITIONAL, true);
		checkParse("((1 > 2) < 3)", WrapperType.CONDITIONAL, true);
		checkParse("((1 > 2) < !3)", WrapperType.CONDITIONAL, true);
		checkParse("((1 > --2) < !3)", WrapperType.CONDITIONAL, true);
		checkParse("((1 -> --2) < !3)", WrapperType.CONDITIONAL, false);
		checkParse("((1 => --2) < !3)", WrapperType.CONDITIONAL, false);
		checkParse("((1 => --2) = 3)", WrapperType.CONDITIONAL, false);
		checkParse("((1 >= --2) == 3)", WrapperType.CONDITIONAL, true);
		checkParse("((1 >= --2) => 3)", WrapperType.CONDITIONAL, false);
		checkParse("((1 >= --2) == (3))", WrapperType.CONDITIONAL, true);
		checkParse("1 == ()", WrapperType.CONDITIONAL, false);
		checkParse("1 == (true)", WrapperType.CONDITIONAL, true);
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
