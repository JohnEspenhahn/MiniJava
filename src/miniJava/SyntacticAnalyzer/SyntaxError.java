package miniJava.SyntacticAnalyzer;

import java.util.stream.Stream;

public class SyntaxError extends Exception {
	private static final long serialVersionUID = 2583239898992018218L;

	SyntaxError(String s) {
		super(s);
	}
	
	SyntaxError(SourceStream ss, TokenKind expected, Token got) {
		super(String.format("%s\nExpected token %s, but got %s", ss.getErrorMark(got), expected.toString(), got.toString()));
	}
	
	SyntaxError(SourceStream ss, TokenKind[] expected, Token got) {
		super(String.format("%s\nExpected one of tokens %s; but got %s", ss.getErrorMark(got),
				String.join(",", Stream.of(expected).map(TokenKind::toString).toArray(String[]::new)), got.toString()));
	}

}