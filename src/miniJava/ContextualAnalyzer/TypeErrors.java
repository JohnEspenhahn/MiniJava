package miniJava.ContextualAnalyzer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import miniJava.ContextualAnalyzer.Exceptions.TypeException;

public class TypeErrors implements Iterable<TypeException> {

	private List<TypeException> errors;
	
	public TypeErrors() {
		errors = new LinkedList<TypeException>();
	}
	
	public void add(TypeException e) {
		this.errors.add(e);
	}
	
	public int size() {
		return errors.size();
	}

	@Override
	public Iterator<TypeException> iterator() {
		return errors.iterator();
	}
}
