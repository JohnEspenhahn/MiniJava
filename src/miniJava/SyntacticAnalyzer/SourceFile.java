package miniJava.SyntacticAnalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SourceFile extends SourceStream {
	private File f;
	
	public SourceFile(File f) throws FileNotFoundException {
		super(new FileInputStream(f));
		this.f = f;
	}
	
	public String getErrorMark(Token t) {
		StringBuffer line1 = new StringBuffer(String.format("[%d] ", t.getStart().getLine()));
		StringBuffer line2 = new StringBuffer(new String(new char[line1.length()]).replace('\0', ' '));		
		
		FileInputStream fis = null;
		try {
			int line = 1, col = 1;
			fis = new FileInputStream(this.f);
			
			// Get to start line
			while (line < t.getStart().getLine()) {
				int c = fis.read();
				if (c == -1) break;
				else if (c == eol) line++;
			}
			
			int startLine = t.getStart().getLine(),
					endLine = t.getEnd().getLine(),
					startCol = t.getStart().getCol(),
					endCol = t.getEnd().getCol();
			while (true) {
				int c = fis.read();
				if (c == -1) break;
				else if (c == eol) { // Only print first line token is on
					if (line != t.getEnd().getLine()) {
						line1.append("  ...");
						line2.append("  ...");
					}
					break;
				} else if (c == '\r')
					continue;
				
				line1.append((char) c);
				if (startLine == endLine && col > startCol && col <= endCol)
					line2.append("^");
				else if (line == startLine && line != endLine && col > startCol)
					line2.append("^");
				else
					line2.append(" ");
				
				col++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) 
				try {
					fis.close();
				} catch (Exception e) { }
		}
		
		return line1.toString() + "\n" + line2.toString();
	}
}
