package miniJava.CodeGenerator.RuntimeDescription;

public abstract class RelativeAddress extends RuntimeDescription {
	private int offset;
	
	public RelativeAddress(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return this.offset;
	}

}
