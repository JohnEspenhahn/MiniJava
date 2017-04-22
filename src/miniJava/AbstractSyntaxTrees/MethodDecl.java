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

	private ParameterDeclList parameterDeclList;
	private StatementList statementList;
	
	private int address;
	private ArrayList<Integer> toPatch; 
	
	public MemberDecl md;

	public MethodDecl(MemberDecl md, ParameterDeclList pl, StatementList sl) {
		this(md, pl, sl, SourcePosition.ZERO);
	}

	public MethodDecl(MemberDecl md, ParameterDeclList pl, StatementList sl, SourcePosition posn) {
		super(md, posn);
		
		this.md = md;
		parameterDeclList = pl;
		statementList = sl;
		
		toPatch = new ArrayList<Integer>();
		address = -1;
	}

	public Object visit(Visitor v, Object o) {
		return v.visitMethodDecl(this, o);
	}
	
	public int getParamCount() {
		return parameterDeclList.size();
	}
	
	public ParameterDecl getParameter(int i) {
		return parameterDeclList.get(i);
	}
	
	public int getStmtCount() {
		return statementList.size();
	}
	
	public Statement getStatement(int i) {
		return statementList.get(i);
	}
	
	public void appendStatement(Statement s) {
		statementList.add(s);
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
