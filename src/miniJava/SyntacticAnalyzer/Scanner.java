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
		LinePosition start = null;
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
		System.out.println(t.getKind());
		
		return t;
	}

	private TokenKind scanToken() {
		switch (cc) {
		case '>':
			takeIt();
			if (cc == '=') {
				takeIt();
				return TokenKind.GTR_EQU;
			} else return TokenKind.GTR;
		case '<':
			takeIt();
			if (cc == '=') {
				takeIt();
				return TokenKind.LSS_EQU;
			} else return TokenKind.LSS;
		case '!':
			takeIt();
			if (cc == '=') {
				takeIt();
				return TokenKind.NOT_EQU;
			} else return TokenKind.NOT;
		case '&':
			takeIt();
			if (cc == '&') {
				takeIt();
				return TokenKind.AND;
			} else return TokenKind.ERROR;
		case '|':
			takeIt();
			if (cc == '|') {
				takeIt();
				return TokenKind.OR;
			} else return TokenKind.ERROR;
		case '=':
			takeIt();
			if (cc == '=') {
				takeIt();
				return TokenKind.EQU;
			} else return TokenKind.ASSIGN;
		case '+':
			takeIt();
			return TokenKind.PLUS;
		case '-':
			takeIt();
			return TokenKind.MINUS;
		case '*':
			takeIt();
			return TokenKind.MULT;
		case '/':
			takeIt();
			if (cc == '*') {
				// Comment block
				skipCommentBlock();
				return null;
			} else if (cc == '/') {
				// Comment line
				skipCommentLine();
				return null;
			} else return TokenKind.DIV;
		case '[':
			takeIt();
			return TokenKind.SQR_OPEN;
		case ']':
			takeIt();
			return TokenKind.SQR_CLOSE;
		case '(':
			takeIt();
			return TokenKind.PAREN_OPEN;
		case ')':
			takeIt();
			return TokenKind.PAREN_CLOSE;
		case '{':
			takeIt();
			return TokenKind.CURL_OPEN;
		case '}':
			takeIt();
			return TokenKind.CURL_CLOSE;
		case '.':
			takeIt();
			return TokenKind.DOT;
		case ',':
			takeIt();
			return TokenKind.COMMA;
		case ';':
			takeIt();
			return TokenKind.SEMICOLON;
		case '0': case '1': case '2': case '3':
		case '4': case '5': case '6': case '7':
		case '8': case '9':
			takeIt();
			while (isDigit(cc)) takeIt();
			return TokenKind.NUM;
		case 'a':  case 'b':  case 'c':  case 'd':  case 'e':
	    case 'f':  case 'g':  case 'h':  case 'i':  case 'j':
	    case 'k':  case 'l':  case 'm':  case 'n':  case 'o':
	    case 'p':  case 'q':  case 'r':  case 's':  case 't':
	    case 'u':  case 'v':  case 'w':  case 'x':  case 'y':
	    case 'z':
	    case 'A':  case 'B':  case 'C':  case 'D':  case 'E':
	    case 'F':  case 'G':  case 'H':  case 'I':  case 'J':
	    case 'K':  case 'L':  case 'M':  case 'N':  case 'O':
	    case 'P':  case 'Q':  case 'R':  case 'S':  case 'T':
	    case 'U':  case 'V':  case 'W':  case 'X':  case 'Y':
	    case 'Z':
	    	takeIt();
	      	while (isLetter(cc) || isDigit(cc) || cc == '_') 
    	  		takeIt();
      		return TokenKind.IDENTIFIER;
	    case SourceFile.eot:
	    	return TokenKind.EOT;
  		default:
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
	
	private void skipCommentBlock() {
		while (cc != SourceFile.eot) {
			skipIt();
			if (cc == '*') {
				skipIt();
				if (cc == '/') {
					skipIt();
					return;
				}
			}
		}
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
