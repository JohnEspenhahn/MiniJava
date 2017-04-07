/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import java.util.ArrayList;

import mJAM.Machine;
import miniJava.SyntacticAnalyzer.SourcePosition;

public class MethodDecl extends MemberDecl {

	public ParameterDeclList parameterDeclList;
	public StatementList statementList;
	
	private int address;
	private ArrayList<Integer> toPatch; 

	public MethodDecl(MemberDecl md, ParameterDeclList pl, StatementList sl) {
		this(md, pl, sl, SourcePosition.ZERO);
	}

	public MethodDecl(MemberDecl md, ParameterDeclList pl, StatementList sl, SourcePosition posn) {
		super(md, posn);
		parameterDeclList = pl;
		statementList = sl;
		
		toPatch = new ArrayList<Integer>();
		address = -1;
	}

	public Object visit(Visitor v, Object o) {
		return v.visitMethodDecl(this, o);
	}
	
	@Override
	public MemberDecl getMember(Identifier ident) {
		return null; // Method's don't directly have members (must invoke first)
	}
	
	public void setAddr(int addr) {
		this.address = addr;
		
		// Update watching
		while (!this.toPatch.isEmpty()) {
			int addrToPatch = this.toPatch.remove(this.toPatch.size()-1);
			Machine.patch(addrToPatch, this.address);
		}
	}
	
	/**
	 * Ask for the given instruction address to be patched to linked to this method's label
	 * @param addr The address of the instruction to be patched
	 */
	public void patch(int addr) {
		if (this.address < 0) {
			// Not ready, add to watching
			this.toPatch.add(addr);
		} else {
			Machine.patch(addr, this.address);
		}
	}

}
