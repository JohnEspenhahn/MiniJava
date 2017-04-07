package miniJava.CodeGenerator.RuntimeDescription;

import miniJava.CodeGenerator.RuntimeModifier.AbsoluteAddressRuntimeModifier;
import miniJava.CodeGenerator.RuntimeModifier.RuntimeModifier;

public class AbsoluteAddress extends RuntimeDescription {
	private int address;
	
	public AbsoluteAddress(int address) {
		this.address = address;
	}
	
	public int getAddress() {
		return this.address;
	}

	@Override
	public RuntimeModifier toBaseRuntimeModifier() {
		return new AbsoluteAddressRuntimeModifier(this);
	}
	
}
