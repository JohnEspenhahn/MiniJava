package miniJava.SyntacticAnalyzer;

public class LinePosition {
	private int line, col;
	
	public LinePosition(int line, int col) {
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
