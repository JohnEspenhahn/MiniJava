package miniJava.CodeGenerator.RuntimeModifier;

import mJAM.Machine;
import mJAM.Machine.Prim;
import miniJava.AbstractSyntaxTrees.Expression;
import miniJava.AbstractSyntaxTrees.Visitor;

public class IndexQualifiedRuntimeModifier extends QualifiedRuntimeModifier {

	public IndexQualifiedRuntimeModifier(RuntimeModifier base, Expression idxExpr) {
		super(base, new RuntimeOffsetGenerator() {

			@Override
			public void load(Visitor visitor) {
				idxExpr.visit(visitor, null);				
			}
			
		});
	}

	@Override
	protected void emitStorePrim() {
		Machine.emit(Prim.arrayupd);	
	}

	@Override
	protected void emitLoadPrim() {
		Machine.emit(Prim.arrayref);		
	}

}
