package miniJava.SyntacticAnalyzer;

import static miniJava.SyntacticAnalyzer.TokenKind.*;
import java.util.Arrays;

public class Parser {
	private Scanner lexer;
	private Token ct;

	public Parser(Scanner lexer) {
		this.lexer = lexer;
	}

	private void acceptIt() {
		ct = this.lexer.scan();
	}

	private void accept(TokenKind expected) throws SyntaxError {
		if (ct.getKind() == expected) acceptIt();
		else throw new SyntaxError(lexer.getSourceFile(), expected, ct);
	}
	
	public boolean parseProgram() {
		return parseProgram(true);
	}

	public boolean parseProgram(boolean print_stacktrace) {
		// Load first token
		this.ct = this.lexer.scan();

		try {
			while (ct.getKind() == CLASS) {
				parseClassDec();
			}

			if (ct.getKind() != EOT) {
				throw new SyntaxError(lexer.getSourceFile(), EOT, ct);
			}
		} catch (SyntaxError e) {
			if (print_stacktrace) e.printStackTrace();
			else System.err.println(e.getMessage());
			return false;
		}
		
		return true;
	}

	private void parseClassDec() throws SyntaxError {
		accept(CLASS);
		accept(IDENTIFIER);
		accept(CURL_OPEN);
		
		// While look ahead is in starters[[Declare]]
		TokenKind k = ct.getKind();
		while (k == PUBLIC || k == PRIVATE || k == STATIC || k == VOID 
				|| k == INT || k == BOOLEAN || k == IDENTIFIER) {
			parseDeclare();
			k = ct.getKind();
		}
		
		accept(CURL_CLOSE);
	}
	
	private void parseDeclare() throws SyntaxError {
		// Visibility
		if (ct.getKind() == PUBLIC || ct.getKind() == PRIVATE) acceptIt();
		
		// Access
		if (ct.getKind() == STATIC) acceptIt();
		
		if (ct.getKind() == VOID) parseVoidMethod();
		else parseTypedDeclare();
	}
	
	private void parseTypedDeclare() throws SyntaxError { 
		parseType();
		accept(IDENTIFIER);
		
		if (ct.getKind() == SEMICOLON) acceptIt();
		else parseMethodContent();
	}
	
	private void parseVoidMethod() throws SyntaxError {
		accept(VOID);
		accept(IDENTIFIER);
		parseMethodContent();
	}
	
	private void parseMethodContent() throws SyntaxError {
		accept(PAREN_OPEN);
		
		if (ct.getKind() == PAREN_CLOSE) {
			acceptIt();
		} else {
			parseParamList();
			accept(PAREN_CLOSE);
		}
		
		accept(CURL_OPEN);
		
		// While look ahead is in starters[[Statement]]
		while (Arrays.binarySearch(STARTERS_STATEMENT, ct.getKind()) >= 0) {
			parseStatement();
		}
		
		accept(CURL_CLOSE);
	}
	
	private void parseType() throws SyntaxError {
		switch (ct.getKind()) {
		case IDENTIFIER:
			parseIdType();
			break;
		case INT: case BOOLEAN:
			parsePrimativeType();
			break;
		default:
			throw new SyntaxError(lexer.getSourceFile(), new TokenKind[] { INT, IDENTIFIER, BOOLEAN }, ct);
		}
	}
	
	private void parsePrimativeType() throws SyntaxError {
		switch (ct.getKind()) {
		case INT:
			acceptIt();
			if (ct.getKind() == SQR_OPEN) {
				accept(SQR_OPEN);
				accept(SQR_CLOSE);
			}
			break;
		case BOOLEAN:
			acceptIt();
			break;
		default:
			throw new SyntaxError(lexer.getSourceFile(), new TokenKind[] { INT, BOOLEAN }, ct);
		}
	}
	
	private void parseIdType() throws SyntaxError {
		accept(IDENTIFIER);
		if (ct.getKind() == SQR_OPEN) {
			accept(SQR_OPEN);
			accept(SQR_CLOSE);
		}
	}
	
	private void parseParamList() throws SyntaxError {
		parseType();
		accept(IDENTIFIER);
		
		while (ct.getKind() == COMMA) {
			accept(COMMA);
			parseType();
			accept(IDENTIFIER);
		}
	}
	
	private void parseArgList() throws SyntaxError {
		parseExpression();
		
		while (ct.getKind() == COMMA) {
			accept(COMMA);
			parseExpression();
		}
	}
	
	private void parseReference() throws SyntaxError {
		Token first_identifier = ct;
		if (ct.getKind() == THIS) {
			acceptIt();
		} else {
			accept(IDENTIFIER);
			if (ct.getKind() == SQR_OPEN)
				parseIndexing();
		}
		
		parseReferenceExtension();
	}	
	private void parseReferenceExtension() throws SyntaxError {
		while(ct.getKind() == DOT) {
			acceptIt();
			accept(IDENTIFIER);
			if (ct.getKind() == SQR_OPEN)
				parseIndexing();
		}
	}
	
	private void parseIndexing() throws SyntaxError {
		accept(SQR_OPEN);
		parseExpression();
		accept(SQR_CLOSE);
	}
	
	// Kinda helps with organization, overhead might not be worth it
	private static TokenKind[] STARTERS_STATEMENT = new TokenKind[] { 
			CURL_OPEN, RETURN, IF, WHILE, INT, BOOLEAN, IDENTIFIER, THIS 
		};
	static { Arrays.sort(STARTERS_STATEMENT); }
	
	private void parseStatement() throws SyntaxError {
		switch (ct.getKind()) {
		case CURL_OPEN:
			acceptIt();
			while (Arrays.binarySearch(STARTERS_STATEMENT, ct.getKind()) >= 0)
				parseStatement();
			accept(CURL_CLOSE);
			break;
		case RETURN:
			acceptIt();
			if (Arrays.binarySearch(STARTERS_EXPRESSION, ct.getKind()) >= 0)
				parseExpression();
			accept(SEMICOLON);
			break;
		case IF:
			acceptIt();
			accept(PAREN_OPEN);
			parseExpression();
			accept(PAREN_CLOSE);
			parseStatement();
			if (ct.getKind() == ELSE) {
				acceptIt();
				parseStatement();
			}
			break;
		case WHILE:
			acceptIt();
			accept(PAREN_OPEN);
			parseExpression();
			accept(PAREN_CLOSE);
			parseStatement();
			break;
		case INT: case BOOLEAN:
			parsePrimativeType();
			accept(IDENTIFIER);
			parseAssign();
			accept(SEMICOLON);
			break;
		case IDENTIFIER:
			// Token first_id = ct;
			acceptIt();
			if (ct.getKind() == SQR_OPEN) {
				// Array. Could still either be: TypeId id Define | Reference ( Assign | Invoke )
				accept(SQR_OPEN);
				if (Arrays.binarySearch(STARTERS_EXPRESSION, ct.getKind()) >= 0) {
					// If there is an Expression in square brackets, it's: Reference ( Assign | Invoke )
					parseExpression();
					accept(SQR_CLOSE);
					parseReferenceStatement();
				} else {
					// Otherwise just an array type: TypeId id Define
					accept(SQR_CLOSE);
					accept(IDENTIFIER);
					parseAssign();
				}
			} else if (ct.getKind() == IDENTIFIER) {
				// Got "id id", must be: IdType id Assign
				accept(IDENTIFIER);
				parseAssign();
			} else {
				// Only other thing it could be is a Reference
				parseReferenceStatement();
			}
			accept(SEMICOLON);
			break;
		case THIS: // TODO We are modifying this - allowed in original grammar, but seems illegal?
			parseReference();
			if (ct.getKind() == ASSIGN) parseAssign();
			else if (ct.getKind() == PAREN_OPEN) parseInvoke();
			else throw new SyntaxError(lexer.getSourceFile(), new TokenKind[] { ASSIGN, PAREN_OPEN }, ct);
			accept(SEMICOLON);
			break;
		default:
			throw new SyntaxError(lexer.getSourceFile(), STARTERS_STATEMENT, ct);
		}
	}
	
	private void parseReferenceStatement() throws SyntaxError {
		parseReferenceExtension();
		
		// Reference ( Assign | Invoke ) 
		if (ct.getKind() == ASSIGN) parseAssign();
		else if (ct.getKind() == PAREN_OPEN) parseInvoke();
		else throw new SyntaxError(lexer.getSourceFile(), new TokenKind[] { DOT, ASSIGN, PAREN_OPEN }, ct);
	}
	
	private void parseAssign() throws SyntaxError {
		accept(ASSIGN);
		parseExpression();
	}
	
	private void parseInvoke() throws SyntaxError {
		accept(PAREN_OPEN);
		if (Arrays.binarySearch(STARTERS_EXPRESSION, ct.getKind()) >= 0)
			parseArgList();
		accept(PAREN_CLOSE);
	}
	
	// Kinda helps with organization, overhead might not be worth it
	private static TokenKind[] STARTERS_EXPRESSION = new TokenKind[] { 
			PAREN_OPEN, NUM, TRUE, FALSE, NEW, 
			NOT, MINUS, // unop 
			THIS, IDENTIFIER // STARTERS_REFERENCE
		};
	static { Arrays.sort(STARTERS_EXPRESSION); }
	
	private void parseExpression() throws SyntaxError {
		parseDisjunction();
	}
	
	private void parseDisjunction() throws SyntaxError {
		parseConjunction();
		while (ct.getKind() == TokenKind.OR) {
			acceptIt();
			parseConjunction();
		}
	}
	
	private void parseConjunction() throws SyntaxError {
		parseEquality();
		while (ct.getKind() == TokenKind.AND) {
			acceptIt();
			parseEquality();
		}
	}
	
	private void parseEquality() throws SyntaxError {
		parseRelational();
		while (ct.getKind() == TokenKind.EQU || ct.getKind() == TokenKind.NOT_EQU) {
			acceptIt();
			parseRelational();
		}
	}
	
	private void parseRelational() throws SyntaxError {
		parseAdditive();
		while (ct.getKind() == TokenKind.LSS || ct.getKind() == TokenKind.LSS_EQU
				|| ct.getKind() == TokenKind.GTR || ct.getKind() == TokenKind.GTR_EQU) {
			acceptIt();
			parseAdditive();
		}
	}
	
	private void parseAdditive() throws SyntaxError {
		parseMultiplicative();
		while (ct.getKind() == TokenKind.PLUS || ct.getKind() == TokenKind.MINUS) {
			acceptIt();
			parseMultiplicative();
		}
	}
	
	private void parseMultiplicative() throws SyntaxError {
		parseValue();
		while (ct.getKind() == TokenKind.MULT || ct.getKind() == TokenKind.DIV) {
			acceptIt();
			parseValue();
		}
	}
	
	private void parseValue() throws SyntaxError {
		switch (ct.getKind()) {
		case PAREN_OPEN:
			acceptIt();
			parseExpression();
			accept(PAREN_CLOSE);
			break;
		case NUM: case TRUE: case FALSE:
			acceptIt();
			break;
		case NEW:
			acceptIt();
			if (ct.getKind() == IDENTIFIER) {
				// Object or object array
				acceptIt();
				if (ct.getKind() == PAREN_OPEN) {
					accept(PAREN_OPEN);
					accept(PAREN_CLOSE);
				} else if (ct.getKind() == SQR_OPEN) {
					parseIndexing();
				}
			} else {
				// Primitive array
				accept(INT);
				if (ct.getKind() == SQR_OPEN) parseIndexing();
			}
			break;
		case NOT: case MINUS: // unop
			acceptIt();
			parseValue();
			break;
		default:
			parseReference();
			if (ct.getKind() == PAREN_OPEN) {
				// Function call
				parseInvoke();
			}
			break;
		}
	}
	
	private boolean isBinOp(TokenKind k) {
		return k == GTR || k == LSS || k == EQU || k == LSS_EQU || k == GTR_EQU || k == NOT_EQU
				|| k == AND || k == OR || k == NOT || k == PLUS || k == MINUS || k == MULT || k == DIV;
	}
}
