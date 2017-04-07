/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import mJAM.Machine.Reg;
import miniJava.CodeGenerator.RuntimeDescription.RelativeAddress;
import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class LocalDecl extends Declaration {	
	public LocalDecl(String name, TypeDenoter t) {
		this(name, t, SourcePosition.ZERO);
	}
	
	public LocalDecl(String name, TypeDenoter t, SourcePosition posn){
		super(name,t,posn);
	}
	
	@Override
	public boolean allowStaticReference() {
		return true;
	}
	
	public void setLocalOffset(int offset) {
		this.rd = new RelativeAddress(Reg.LB, offset);
	}
}
