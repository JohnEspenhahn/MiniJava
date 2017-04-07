package miniJava.CodeGenerator.RuntimeDescription;

import mJAM.Machine.Reg;
import miniJava.CodeGenerator.RuntimeModifier.RegisterRuntimeModifier;
import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;

public class ObjectRelativeAddress extends RelativeAddress {

	public ObjectRelativeAddress(int offset) {
		super(offset);
	}

	@Override
	public RuntimeModifier toBaseRuntimeModifier() {
		return new RegisterRuntimeModifier(Reg.OB, this);
	}

}
