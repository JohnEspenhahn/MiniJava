package miniJava.SyntacticAnalyzer;

public class Token {	
	private TokenKind kind;
	private String spelling;
	private SourcePosition start, end;
	
	public Token(TokenKind kind, String spelling, SourcePosition start, SourcePosition end) {
		/*
		if ((start == SourcePosition.ZERO || end == SourcePosition.ZERO) && !ALLOW_DEBUG) {
			throw new RuntimeException("Provided SourcePosition.ZERO while not in debug mode!");
		}
		*/
		
		this.kind = kind;
		this.spelling = spelling;
		this.start = start;
		this.end = end;
		
		if (kind == TokenKind.IDENTIFIER) {
			this.kind = updateKeywordToken();
		} else if (kind == TokenKind.CHAR) {
            this.kind = TokenKind.NUM;
            this.spelling = "" + (int) this.spelling.charAt(1);
        }

	}
	
	public Token(TokenKind kind, String spelling) {
		this(kind, spelling, SourcePosition.ZERO, SourcePosition.ZERO);
	}
	
	public TokenKind getKind() {
		return this.kind;
	}
	
	public String getSpelling() {
		return this.spelling;
	}
	
	public SourcePosition getStart() {
		return this.start;
	}
	
	public SourcePosition getEnd() {
		return this.end;
	}
	
	private TokenKind updateKeywordToken() {
		if (spelling.equals("class")) return TokenKind.CLASS;
		else if (spelling.equals("void")) return TokenKind.VOID;
		else if (spelling.equals("public")) return TokenKind.PUBLIC;
		else if (spelling.equals("private")) return TokenKind.PRIVATE;
		else if (spelling.equals("static")) return TokenKind.STATIC;
		else if (spelling.equals("int")) return TokenKind.INT;
		else if (spelling.equals("boolean")) return TokenKind.BOOLEAN;
		else if (spelling.equals("this")) return TokenKind.THIS;
		else if (spelling.equals("if")) return TokenKind.IF;
		else if (spelling.equals("while")) return TokenKind.WHILE;
		else if (spelling.equals("else")) return TokenKind.ELSE;
		else if (spelling.equals("true")) return TokenKind.TRUE;
		else if (spelling.equals("false")) return TokenKind.FALSE;
		else if (spelling.equals("new")) return TokenKind.NEW;
		else if (spelling.equals("return")) return TokenKind.RETURN;
		else if (spelling.equals("null")) return TokenKind.NULL;
		
		// Default
		return TokenKind.IDENTIFIER;
	}
	
	public String toString() {
		return String.format("%s[%s]", getKind().toString(), getSpelling());
	}
}
