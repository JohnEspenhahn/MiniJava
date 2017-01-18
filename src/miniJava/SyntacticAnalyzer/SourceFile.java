package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;

public class SourceFile {
	public static final char eol = '\n';
	public static final char eot = '\u0000';

	private InputStream stream;
	private int currentLine;

	public SourceFile(InputStream stream) {
		this.stream = stream;
		this.currentLine = 1;
	}

	public char getNext() {
		try {
			int c = stream.read();

			if (c == -1) {
				c = eot;
			} else if (c == eol) {
				currentLine++;
			}
			return (char) c;
		} catch (IOException s) {
			return eot;
		}
	}

	public int getCurrentLine() {
		return currentLine;
	}
}
