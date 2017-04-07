package miniJava.CodeGenerator.RuntimeDescription;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;

public class AbsoluteAddress extends RuntimeDescription {

	private int address;
	
	public AbsoluteAddress(int address) {
		this.address = address;
	}
	
	@Override
	public void store() {
		Machine.emit(Op.STORE, Reg.SB, address);
	}
	
	@Override
	public void load() {
		Machine.emit(Op.LOAD, Reg.SB, address);
	}
	
}
