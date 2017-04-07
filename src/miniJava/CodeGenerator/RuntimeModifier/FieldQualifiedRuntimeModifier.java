package miniJava.CodeGenerator.RuntimeModifier;

import mJAM.Machine;
import mJAM.Machine.Op;
import mJAM.Machine.Prim;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.CodeGenerator.RuntimeDescription.RelativeAddress;

public class FieldQualifiedRuntimeModifier extends QualifiedRuntimeModifier {

	public FieldQualifiedRuntimeModifier(RuntimeModifier base, RelativeAddress offset) {
		super(base, new RuntimeOffsetGenerator() {
			@Override
			public void load(Visitor visitor) {
				Machine.emit(Op.LOADL, offset.getOffset());
			}
		});
	}

	@Override
	protected void emitStorePrim() {
		Machine.emit(Prim.fieldupd);
	}

	@Override
	protected void emitLoadPrim() {
		Machine.emit(Prim.fieldref);
	}

}
