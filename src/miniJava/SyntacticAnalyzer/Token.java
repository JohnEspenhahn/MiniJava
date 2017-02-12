package miniJava.SyntacticAnalyzer;

public class Token {
	public static boolean ALLOW_DEBUG = false;
	
	private TokenKind kind;
	private String spelling;
	private SourcePosition start, end;
	
	public Token(TokenKind kind, String spelling, SourcePosition start, SourcePosition end) {
		if ((start == SourcePosition.ZERO || end == SourcePosition.ZERO) && !ALLOW_DEBUG) {
			throw new RuntimeException("Provided SourcePosition.ZERO while not in debug mode!");
		}
		
		this.kind = kind;
		this.spelling = spelling;
		this.start = start;
		this.end = end;
		
		if (kind == TokenKind.IDENTIFIER)
			this.kind = updateKeywordToken();
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
		switch (this.spelling) {
		case "class": return TokenKind.CLASS;
		case "void": return TokenKind.VOID;
		case "public": return TokenKind.PUBLIC;
		case "private": return TokenKind.PRIVATE;
		case "static": return TokenKind.STATIC;
		case "int": return TokenKind.INT;
		case "boolean": return TokenKind.BOOLEAN;
		case "this": return TokenKind.THIS;
		case "if": return TokenKind.IF;
		case "while": return TokenKind.WHILE;
		case "else": return TokenKind.ELSE;
		case "true": return TokenKind.TRUE;
		case "false": return TokenKind.FALSE;
		case "new": return TokenKind.NEW;
		case "return": return TokenKind.RETURN;
		default: return TokenKind.IDENTIFIER;
		}
	}
	
	public String toString() {
		return String.format("%s[%s]", getKind().toString(), getSpelling());
	}
}
