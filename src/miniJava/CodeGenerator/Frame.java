package miniJava.CodeGenerator;

public class Frame {
	private int nextLocalBase;
	private int numArgs;
	
	public Frame(int numArgs) {
		this.nextLocalBase = 3;
		this.numArgs = numArgs;
	}
	
	public int getNextLocalBase() {
		return nextLocalBase++;
	}
	
	public int getNumArgs() {
		return this.numArgs;
	}
	
}
