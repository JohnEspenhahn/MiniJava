package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;

public class SourceStream {
	public static final char eol = '\n';
	public static final char eot = '\u0000';

	private InputStream stream;
	private int line, col;

	public SourceStream(InputStream stream) {
		this.stream = stream;
		this.line = 1;
		this.col = 1;
	}

	public char getNext() {
		try {
			int c = stream.read();

			if (c == -1) {
				c = eot;
			} else if (c == eol) {
				this.line++;
				this.col = 1;
			} else {
				this.col++;
			}
			return (char) c;
		} catch (IOException s) {
			return eot;
		}
	}

	public SourcePosition getCurrentPosition() {
		return new SourcePosition(this.line, this.col);
	}
	
	public String getErrorMark(Token t) {
		return String.format("Expected type at line %d, but got %s", t.getStart().getLine(), t);
	}
}
