package miniJava.SyntacticAnalyzer;

import java.util.stream.Stream;

public class SyntaxError extends Exception {
	private static final long serialVersionUID = 2583239898992018218L;

	SyntaxError() {
		super();
	};

	SyntaxError(String s) {
		super(s);
	}
	
	SyntaxError(TokenKind expected, Token got) {
		super(String.format("Expected token %s at line %d, but got %s", expected.toString(), got.getLine(), got.toString()));
	}
	
	SyntaxError(TokenKind[] expected, Token got) {
		super(String.format("Expected one of tokens %s at line %d, but got %s", 
				String.join("|", Stream.of(expected).map(TokenKind::toString).toArray(String[]::new)), 
				got.getLine(), got.toString()));
	}

}