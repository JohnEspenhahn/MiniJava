package miniJava.SyntacticAnalyzer;

public class SourcePosition {
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
