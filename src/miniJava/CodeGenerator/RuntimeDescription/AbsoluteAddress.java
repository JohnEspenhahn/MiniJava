package miniJava.CodeGenerator.RuntimeDescription;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;

public class AbsoluteAddress implements RuntimeDescription, RuntimeModifier {

	private int address;
	
	public AbsoluteAddress(int address) {
		this.address = address;
	}
	
	@Override
	public void store(Visitor<Object,Object> visitor, AST value) {
		value.visit(visitor, null);
		Machine.emit(Op.STORE, Reg.SB, address);
	}
	
	@Override
	public void load(Visitor<Object,Object> visitor) {
		Machine.emit(Op.LOAD, Reg.SB, address);
	}

	@Override
	public RuntimeModifier toBaseRuntimeModifier() {
		return this;
	}
	
}
