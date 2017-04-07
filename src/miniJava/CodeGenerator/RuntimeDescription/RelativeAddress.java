package miniJava.CodeGenerator.RuntimeDescription;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;

public class RelativeAddress extends RuntimeDescription {
	private Reg reg;
	private int offset;
	
	public RelativeAddress(Reg reg, int offset) {
		this.reg = reg;
		this.offset = offset;
	}
	
	@Override
	public void store() {
		Machine.emit(Op.STORE, reg, offset);
	}
	
	@Override
	public void load() {
		Machine.emit(Op.LOAD, reg, offset);
	}
	
	public int getOffset() {
		return this.offset;
	}
	
}
