package miniJava.SyntacticAnalyzer;

import java.util.stream.Stream;

@SuppressWarnings("serial")
public class SyntaxError extends Exception {

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