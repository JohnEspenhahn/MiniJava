package miniJava.CodeGenerator.RuntimeDescription;

import mJAM.Machine.Reg;
import miniJava.CodeGenerator.RuntimeModifier.RegisterRuntimeModifier;
import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;

public class LocalsRelativeAddress extends RelativeAddress {

	public LocalsRelativeAddress(int offset) {
		super(offset);
	}

	@Override
	public RuntimeModifier toBaseRuntimeModifier() {
		return new RegisterRuntimeModifier(Reg.LB, this);
	}

}
