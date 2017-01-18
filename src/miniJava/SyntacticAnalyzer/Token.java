package miniJava.SyntacticAnalyzer;

public class Token {
	private TokenKind kind;
	private String spelling;
	
	public Token(TokenKind kind, String spelling) {
		this.kind = kind;
		this.spelling = spelling;
		
		if (kind == TokenKind.IDENTIFIER)
			this.kind = updateKeywordToken();
	}
	
	public TokenKind getKind() {
		return this.kind;
	}
	
	public String getSpelling() {
		return this.spelling;
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
}
