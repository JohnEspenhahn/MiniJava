package miniJava.CodeGenerator.RuntimeModifier;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.CodeGenerator.RuntimeDescription.RelativeAddress;

public class RegisterRuntimeModifier extends RuntimeModifier {
	private Reg reg;
	private RelativeAddress offset;
	
	public RegisterRuntimeModifier(Reg reg, RelativeAddress offset) {
		this.reg = reg;
		this.offset = offset;
	}
	
	@Override
	public void store(Visitor visitor, AST value) {
		value.visit(visitor, null);
		Machine.emit(Op.STORE, reg, offset.getOffset());
	}
	
	@Override
	public void load(Visitor visitor) {
		Machine.emit(Op.LOAD, reg, offset.getOffset());
	}
	
}
