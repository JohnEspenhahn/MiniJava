package miniJava.CodeGenerator.RuntimeModifier;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Visitor;

public class ThisRuntimeModifier extends RuntimeModifier {

	@Override
	public void store(Visitor visitor, AST value) {
		throw new RuntimeException("Cannot store to this!");
	}

	@Override
	public void load(Visitor visitor) {
		Machine.emit(Op.LOADA, Reg.OB, 0);
	}

}
