package miniJava.SyntacticAnalyzer;

public class Scanner {
	private SourceStream src;
	private StringBuffer currentSpelling;
	private char cc;

	public Scanner(SourceStream src) {
		this.src = src;
		this.cc = src.getNext(); 
		
		this.currentSpelling = new StringBuffer();
	}
	
	public SourceStream getSourceFile() {
		return this.src;
	}

	public Token scan() {
		// Get token
		SourcePosition start = null;
		TokenKind kind = null;
		do {
			// Skip whitespace
			while (cc == ' ' || cc == '\t' || cc == '\n' || cc == '\r')
				this.skipIt();
			
			start = src.getCurrentPosition();
			kind = scanToken();
		} while (kind == null); // Comments return null
		
		Token t = new Token(kind, currentSpelling.toString(), start, src.getCurrentPosition());
		this.currentSpelling = new StringBuffer();
		// System.out.println(t);
		
		return t;
	}

	private TokenKind scanToken() {
		if (cc == '>') {
			takeIt();
			if (cc == '=') {
				takeIt();
				return TokenKind.GTR_EQU;
			} else return TokenKind.GTR;
		} else if (cc == '<') {
			takeIt();
			if (cc == '=') {
				takeIt();
				return TokenKind.LSS_EQU;
			} else return TokenKind.LSS;
		} else if (cc == '!') {
			takeIt();
			if (cc == '=') {
				takeIt();
				return TokenKind.NOT_EQU;
			} else return TokenKind.NOT;
		} else if (cc == '&') {
			takeIt();
			if (cc == '&') {
				takeIt();
				return TokenKind.AND;
			} else return TokenKind.ERROR;
		} else if (cc == '|') {
			takeIt();
			if (cc == '|') {
				takeIt();
				return TokenKind.OR;
			} else return TokenKind.ERROR;
		} else if (cc == '=') {
			takeIt();
			if (cc == '=') {
				takeIt();
				return TokenKind.EQU;
			} else return TokenKind.ASSIGN;
		} else if (cc == '+') {
			takeIt();
			return TokenKind.PLUS;
		} else if (cc == '-') {
			takeIt();
			if (cc == '-') return TokenKind.DECREMENT;
			else return TokenKind.MINUS;
		} else if (cc == '*') {
			takeIt();
			return TokenKind.MULT;
		} else if (cc == '/') {
			takeIt();
			if (cc == '*') {
				// Comment block (clear taken '/')
				currentSpelling = new StringBuffer();
				return skipCommentBlock();
			} else if (cc == '/') {
				// Comment line (clear taken '/')
				currentSpelling = new StringBuffer();
				skipCommentLine();
				return null;
			} else {
				return TokenKind.DIV;
			}
		} else if (cc == '[') {		
			takeIt();
			return TokenKind.SQR_OPEN;
		} else if (cc == ']') {
			takeIt();
			return TokenKind.SQR_CLOSE;
		} else if (cc == '(') {
			takeIt();
			return TokenKind.PAREN_OPEN;
		} else if (cc == ')') {
			takeIt();
			return TokenKind.PAREN_CLOSE;
		} else if (cc == '{') {
			takeIt();
			return TokenKind.CURL_OPEN;
		} else if (cc == '}') {
			takeIt();
			return TokenKind.CURL_CLOSE;	
		} else if (cc == '.') {
			takeIt();
			return TokenKind.DOT;
		} else if (cc == ',') {
			takeIt();
			return TokenKind.COMMA;
		} else if (cc == ';') {
			takeIt();
			return TokenKind.SEMICOLON;
		} else if (cc == '\'') {
            takeIt();
            if (cc == '\\') {
                // Convert escape character
                skipIt();
                if (cc == 'n') cc = 10;
                else if (cc == 'r') cc = 13;
                else if (cc == 't') cc = 9; 
                else if (cc == '\\') cc = 92;
                else if (cc == '\'') cc = 39;
                else return TokenKind.ERROR;
                takeIt();
            } else {
                takeIt();
            }
            
            if (cc == '\'') {
                takeIt();
                return TokenKind.CHAR;
            }
            return TokenKind.ERROR;
		} else if (isDigit(cc)) {
			takeIt();
			while (isDigit(cc)) takeIt();
			return TokenKind.NUM;	
		} else if (isLetter(cc)) {
			takeIt();
	      	while (isLetter(cc) || isDigit(cc) || cc == '_') 
    	  		takeIt();
      		return TokenKind.IDENTIFIER;
		} else if (cc == SourceFile.eot) {
			return TokenKind.EOT;
		} else {
			return TokenKind.ERROR;
		}
	}

	private void takeIt() {
		currentSpelling.append(cc);
		cc = src.getNext();
	}
	
	private void skipIt() {
		cc = src.getNext();
	}
	
	private TokenKind skipCommentBlock() {
		boolean matched = false;
		while (cc != SourceFile.eot && !matched) {
			skipIt();
			while (cc == '*') {
				skipIt();
				if (cc == '/') {
					skipIt();
					matched = true;
					break;
				}
			}
		}
		
		if (!matched) return TokenKind.ERROR;
		else return null; // Was safe to skip
	}
	
	private void skipCommentLine() {
		while (true) {
			skipIt();
			if (cc == SourceFile.eol || cc == SourceFile.eot) {
				skipIt();
				return;
			}
		}
	}
	
	private boolean isLetter(char c) {
	    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

}
