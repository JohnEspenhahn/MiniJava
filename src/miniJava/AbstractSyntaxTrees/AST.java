/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class AST {

  public AST (SourcePosition posn) {
	  /*
	  if (posn == SourcePosition.ZERO && !Token.ALLOW_DEBUG) {
		  throw new RuntimeException("Got SourcePosition.ZERO while not debugging!");
	  }
	  */
	  
	  this.posn = posn;
  }

  public abstract Object visit(Visitor v, Object o);

  public SourcePosition posn;
}
