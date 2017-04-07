package miniJava.CodeGenerator.RuntimeModifier;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Visitor;

public interface RuntimeModifier {

	void store(Visitor<Object,Object> visitor, AST value);
	
	void load(Visitor<Object,Object> visitor);
	
}
