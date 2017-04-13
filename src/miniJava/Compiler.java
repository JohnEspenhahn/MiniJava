package miniJava;

import java.io.File;
import java.io.IOException;

import mJAM.Disassembler;
import mJAM.Interpreter;
import mJAM.ObjectFile;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.CodeGenerator.CodeGenVisitor;
import miniJava.ContextualAnalyzer.IdentificationVisitor;
import miniJava.ContextualAnalyzer.TypeErrors;
import miniJava.ContextualAnalyzer.TypeVisitor;
import miniJava.ContextualAnalyzer.Exceptions.TypeException;
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
		if (!f.exists()) {
			System.err.println("No file called '" + args[0] + "' could be found!");
			System.exit(1);
		}

		String fileName = args[0].substring(0, args[0].lastIndexOf('.'));

		try {
			Parser p = new Parser(new Scanner(new SourceFile(f)));
			miniJava.AbstractSyntaxTrees.Package pck = p.parseProgram(false);
			if (pck == null) {
				System.exit(4);
			} else {
				new ASTDisplay().showTree(pck);
				if (new IdentificationVisitor().visit(pck)) {
					TypeErrors errs = new TypeVisitor().visit(pck);
					if (pck.main == null) {
						// Check for required main method
						System.out.print("No main method found");
						System.exit(4);
					} else if (errs.size() > 0) {
						// Output type errors
						for (TypeException e : errs) {
							System.out.println(e.getMessage());
						}
						System.exit(4);
					} else {
						(new CodeGenVisitor()).visit(pck);
						writeObjFiles(fileName + ".mJAM");
						System.exit(0);
					}
				} else {
					System.exit(4);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("An IOException occured!");
			System.exit(1);
		}
	}

	private static void writeObjFiles(String objectCodeFileName) {
		ObjectFile objF = new ObjectFile(objectCodeFileName);
		System.out.print("Writing object code file " + objectCodeFileName + " ... ");
		if (objF.write()) {
			System.out.println("FAILED!");
			return;
		} else {
			System.out.println("SUCCEEDED");
		}

		// create asm file using disassembler
		String asmCodeFileName = objectCodeFileName.replace(".mJAM", ".asm");
		System.out.print("Writing assembly file " + asmCodeFileName + " ... ");
		Disassembler d = new Disassembler(objectCodeFileName);
		if (d.disassemble()) {
			System.out.println("FAILED!");
			return;
		} else {
			System.out.println("SUCCEEDED");
		}

		/*
		 * run code using debugger
		 */
		System.out.println("Running code in debugger ... ");
		Interpreter.debug(objectCodeFileName, asmCodeFileName);
		Interpreter.interpret(objectCodeFileName);

		System.out.println("*** mJAM execution completed");
	}

}
