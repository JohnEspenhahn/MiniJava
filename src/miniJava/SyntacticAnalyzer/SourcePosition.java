package miniJava.SyntacticAnalyzer;

public class SourcePosition {
	public static final SourcePosition ZERO = new SourcePosition(0, 0);
	
	private int line, col;
	
	public SourcePosition(int line, int col) {
		this.line = line;
		this.col = col;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public int getCol() {
		return this.col;
	}
	
}
