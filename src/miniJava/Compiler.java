package miniJava;

import java.io.File;
import java.io.IOException;

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
			if (p.parseProgram(false) == null) System.exit(0);
			else System.exit(4);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("An IOException occured!");
			System.exit(1);
		}
	}
	
}
