package miniJava.test;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.SourceStream;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;
import static miniJava.SyntacticAnalyzer.TokenKind.*;

public class ScannerTest {

	@Test
	public void test() {
		// CLASS DECLARATION
		checkScan("class c { public static boolean b; }", new TokenKind[] {
			CLASS, IDENTIFIER, CURL_OPEN, PUBLIC, STATIC, BOOLEAN, IDENTIFIER, SEMICOLON, CURL_CLOSE	
		});
		// FIELD DECLARATION
		checkScan("public static int log_id_123_a;", new TokenKind[] {
			PUBLIC, STATIC, INT, IDENTIFIER, SEMICOLON
		});
		// METHOD DECLARATION
		checkScan("public void function1(int P1, String p2) { }", new TokenKind[] {
			PUBLIC, VOID, IDENTIFIER, PAREN_OPEN, INT, IDENTIFIER, COMMA, IDENTIFIER, IDENTIFIER, PAREN_CLOSE, CURL_OPEN, CURL_CLOSE
		});
		// VISIBILITY
		checkScan("private", new TokenKind[] { PRIVATE });
		checkScan("public", new TokenKind[] { PUBLIC });
		// ACCESS
		checkScan("static", new TokenKind[] { STATIC });
		// TYPE
		checkScan("int boolean SomeOtherClass int[] Another123[]", new TokenKind[] {
			INT, BOOLEAN, IDENTIFIER, INT, SQR_OPEN, SQR_CLOSE, IDENTIFIER, SQR_OPEN, SQR_CLOSE
		});
		// PARAM LIST
		checkScan("int id1, boolean id2, String id3", new TokenKind[] {
			INT, IDENTIFIER, COMMA, BOOLEAN, IDENTIFIER, COMMA, IDENTIFIER, IDENTIFIER
		});
		// REFERENCE
		checkScan("identifier[1+2-3/4*5]", new TokenKind[] {
			IDENTIFIER, SQR_OPEN, NUM, PLUS, NUM, MINUS, NUM, DIV, NUM, MULT, NUM, SQR_CLOSE
		});
		checkScan("this", new TokenKind[] { THIS });
		checkScan("ref.ref2[1<2]", new TokenKind[] {
			IDENTIFIER, DOT, IDENTIFIER, SQR_OPEN, NUM, LSS, NUM, SQR_CLOSE
		});
		// STATEMENT
		checkScan("return \n\t 1 <= 2;", new TokenKind[] { RETURN, NUM, LSS_EQU, NUM, SEMICOLON });
		checkScan("if (1 == 2) func(); else func2();", new TokenKind[] {
			IF, PAREN_OPEN, NUM, EQU, NUM, PAREN_CLOSE, IDENTIFIER, PAREN_OPEN, PAREN_CLOSE, SEMICOLON,
			ELSE, IDENTIFIER, PAREN_OPEN, PAREN_CLOSE, SEMICOLON
		});
		checkScan("while (id1 && id2 || id3 && !id4 || id5 != id6) { }", new TokenKind[] {
			WHILE, PAREN_OPEN, IDENTIFIER, AND, IDENTIFIER, OR, IDENTIFIER, AND, NOT, IDENTIFIER, OR,
			IDENTIFIER, NOT_EQU, IDENTIFIER, PAREN_CLOSE, CURL_OPEN, CURL_CLOSE
		});
		// EXPRESSION
		checkScan("true false", new TokenKind[] { TRUE, FALSE });
		checkScan("new id(); int[1]; ident[a]", new TokenKind[] {
			NEW, IDENTIFIER, PAREN_OPEN, PAREN_CLOSE, SEMICOLON, INT, SQR_OPEN, NUM, SQR_CLOSE,
			SEMICOLON, IDENTIFIER, SQR_OPEN, IDENTIFIER, SQR_CLOSE
		});
		checkScan("(1 > 2)", new TokenKind[] { PAREN_OPEN, NUM, GTR, NUM, PAREN_CLOSE });
		checkScan("int i = 0;", new TokenKind[] { INT, IDENTIFIER, ASSIGN, NUM, SEMICOLON });
		checkScan("boolean b = i;", new TokenKind[] { BOOLEAN, IDENTIFIER, ASSIGN, IDENTIFIER, SEMICOLON });
		// COMMENT
		checkScan("1 // sdlkf j;askdjf a;lksjfdlk sajdlkf", new TokenKind[] { NUM });
		checkScan("1 /* askdfj lasjfd lkasjfd kajsd;f */ 2 ^ 2", new TokenKind[] { NUM, NUM, ERROR });
		
		// INVALID SCAN
		checkScan(":", new TokenKind[] { ERROR });
	}
	
	private void checkScan(String in, TokenKind[] res) {
		if (res.length == 0) return;
			
		Scanner s = new Scanner(new SourceStream(new ByteArrayInputStream(in.getBytes())));
		
		int idx = 0;
		Token t = null;
		do {
			t = s.scan();
			if (t.getKind() != res[idx]) {
				fail("Unmatched token " + t.getKind() + " and " + res[idx]);
			}
		} while (++idx < res.length);
		
		t = s.scan();
		if (t.getKind() != TokenKind.EOT && res[res.length-1] != TokenKind.ERROR) {
			fail("Unmatched token " + t.getKind() + " at EOT");
		}
	}

}
