package miniJava;

import java.io.File;
import java.io.IOException;

import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.SourceFile;

public class Compiler {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("No filename provided!");
			System.exit(1);
		}
		
		File f = new File(args[0]);
		// File f = new File("pass151.java");
		if (!f.exists()) {
			System.err.println("No file called '" + args[0] + "' could be found!");
			System.exit(1);
		}
		
		try {
			Parser p = new Parser(new Scanner(new SourceFile(f)));
			miniJava.AbstractSyntaxTrees.Package pck = p.parseProgram(false);
			if (pck == null) {
				System.exit(4);
			} else {
				new ASTDisplay().showTree(pck);
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("An IOException occured!");
			System.exit(1);
		}
	}
	
}
