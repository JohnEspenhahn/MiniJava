package miniJava.CodeGenerator.RuntimeModifier;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.CodeGenerator.RuntimeDescription.AbsoluteAddress;

public class AbsoluteAddressRuntimeModifier extends RuntimeModifier {

	private AbsoluteAddress address;
	
	public AbsoluteAddressRuntimeModifier(AbsoluteAddress address) {
		this.address = address;
	}
	
	@Override
	public void store(Visitor visitor, AST value) {
		value.visit(visitor, null);
		Machine.emit(Op.STORE, Reg.SB, address.getAddress());
	}
	
	@Override
	public void load(Visitor visitor) {
		Machine.emit(Op.LOAD, Reg.SB, address.getAddress());
	}
	
}
