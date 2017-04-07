package miniJava.CodeGenerator.RuntimeModifier;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Visitor;

public class ThisRuntimeModifier implements RuntimeModifier {

	@Override
	public void store(Visitor<Object, Object> visitor, AST value) {
		throw new RuntimeException("Cannot store to this!");
	}

	@Override
	public void load(Visitor<Object, Object> visitor) {
		Machine.emit(Op.LOADA, Reg.OB, 0);
	}

}
