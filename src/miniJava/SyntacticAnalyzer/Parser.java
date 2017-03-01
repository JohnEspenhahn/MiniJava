package miniJava.SyntacticAnalyzer;

import static miniJava.SyntacticAnalyzer.TokenKind.ASSIGN;
import static miniJava.SyntacticAnalyzer.TokenKind.BOOLEAN;
import static miniJava.SyntacticAnalyzer.TokenKind.CLASS;
import static miniJava.SyntacticAnalyzer.TokenKind.COMMA;
import static miniJava.SyntacticAnalyzer.TokenKind.CURL_CLOSE;
import static miniJava.SyntacticAnalyzer.TokenKind.CURL_OPEN;
import static miniJava.SyntacticAnalyzer.TokenKind.DOT;
import static miniJava.SyntacticAnalyzer.TokenKind.ELSE;
import static miniJava.SyntacticAnalyzer.TokenKind.EOT;
import static miniJava.SyntacticAnalyzer.TokenKind.FALSE;
import static miniJava.SyntacticAnalyzer.TokenKind.IDENTIFIER;
import static miniJava.SyntacticAnalyzer.TokenKind.NULL;
import static miniJava.SyntacticAnalyzer.TokenKind.IF;
import static miniJava.SyntacticAnalyzer.TokenKind.INT;
import static miniJava.SyntacticAnalyzer.TokenKind.MINUS;
import static miniJava.SyntacticAnalyzer.TokenKind.NEW;
import static miniJava.SyntacticAnalyzer.TokenKind.NOT;
import static miniJava.SyntacticAnalyzer.TokenKind.NUM;
import static miniJava.SyntacticAnalyzer.TokenKind.PAREN_CLOSE;
import static miniJava.SyntacticAnalyzer.TokenKind.PAREN_OPEN;
import static miniJava.SyntacticAnalyzer.TokenKind.PRIVATE;
import static miniJava.SyntacticAnalyzer.TokenKind.PUBLIC;
import static miniJava.SyntacticAnalyzer.TokenKind.RETURN;
import static miniJava.SyntacticAnalyzer.TokenKind.SEMICOLON;
import static miniJava.SyntacticAnalyzer.TokenKind.SQR_CLOSE;
import static miniJava.SyntacticAnalyzer.TokenKind.SQR_OPEN;
import static miniJava.SyntacticAnalyzer.TokenKind.STATIC;
import static miniJava.SyntacticAnalyzer.TokenKind.THIS;
import static miniJava.SyntacticAnalyzer.TokenKind.TRUE;
import static miniJava.SyntacticAnalyzer.TokenKind.VOID;
import static miniJava.SyntacticAnalyzer.TokenKind.WHILE;

import java.util.Arrays;

import miniJava.AbstractSyntaxTrees.ArrayType;
import miniJava.AbstractSyntaxTrees.AssignStmt;
import miniJava.AbstractSyntaxTrees.BaseRef;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BinaryExpr;
import miniJava.AbstractSyntaxTrees.BlockStmt;
import miniJava.AbstractSyntaxTrees.BooleanLiteral;
import miniJava.AbstractSyntaxTrees.CallExpr;
import miniJava.AbstractSyntaxTrees.CallStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassDeclList;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.ExprList;
import miniJava.AbstractSyntaxTrees.Expression;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.FieldDeclList;
import miniJava.AbstractSyntaxTrees.IdRef;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.IfStmt;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.IxIdRef;
import miniJava.AbstractSyntaxTrees.IxQRef;
import miniJava.AbstractSyntaxTrees.LiteralExpr;
import miniJava.AbstractSyntaxTrees.MemberDecl;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodDeclList;
import miniJava.AbstractSyntaxTrees.NewArrayExpr;
import miniJava.AbstractSyntaxTrees.NewObjectExpr;
import miniJava.AbstractSyntaxTrees.NullLiteral;
import miniJava.AbstractSyntaxTrees.Operator;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.ParameterDeclList;
import miniJava.AbstractSyntaxTrees.QRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.Reference;
import miniJava.AbstractSyntaxTrees.ReturnStmt;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.StatementList;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.TypeDenoter;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.WhileStmt;

public class Parser {
	private Scanner lexer;
	private Token ct;

	public Parser(Scanner lexer) {
		this.lexer = lexer;
		this.ct = this.lexer.scan();
	}

	private Token acceptIt() {
		Token res = ct;
		ct = this.lexer.scan();
		return res;
	}

	private Token accept(TokenKind expected) throws SyntaxError {
		if (ct.getKind() == expected) return acceptIt();
		else throw new SyntaxError(lexer.getSourceFile(), expected, ct);
	}
	
	public Package parseProgram() {
		return parseProgram(true);
	}

	public Package parseProgram(boolean print_stacktrace) {
		// Load first token
		Token first = this.ct;

		ClassDeclList classes = new ClassDeclList();
		try {
			while (ct.getKind() == CLASS) {
				classes.add(parseClassDec());
			}

			if (ct.getKind() != EOT) {
				throw new SyntaxError(lexer.getSourceFile(), EOT, ct);
			}
		} catch (SyntaxError e) {
			if (print_stacktrace) e.printStackTrace();
			else System.err.println(e.getMessage());
			return null;
		}
		
		return new Package(classes, first.getStart());
	}

	ClassDecl parseClassDec() throws SyntaxError {
		accept(CLASS);
		Token cn = accept(IDENTIFIER);
		accept(CURL_OPEN);
		
		FieldDeclList fields = new FieldDeclList();
		MethodDeclList methods = new MethodDeclList();
		
		// While look ahead is in starters[[Declare]]
		TokenKind k = ct.getKind();
		while (k == PUBLIC || k == PRIVATE || k == STATIC || k == VOID 
				|| k == INT || k == BOOLEAN || k == IDENTIFIER) {
			MemberDecl member = parseDeclare();
			if (member instanceof FieldDecl) fields.add((FieldDecl) member);
			else if (member instanceof MethodDecl) methods.add((MethodDecl) member);
			else throw new RuntimeException("Unknown MemberDecl type: " + member.getClass());
			
			k = ct.getKind();
		}
		
		accept(CURL_CLOSE);
		
		return new ClassDecl(cn.getSpelling(), fields, methods, cn.getStart());
	}
	
	MemberDecl parseDeclare() throws SyntaxError {
		// Visibility
		boolean isPrivate = false;
		if (ct.getKind() == PUBLIC || ct.getKind() == PRIVATE) {
			isPrivate = (ct.getKind() == PRIVATE);
			acceptIt();
		}
		
		// Access
		boolean isStatic = false;
		if (ct.getKind() == STATIC) {
			isStatic = true;
			acceptIt();
		}
		
		Token id;
		if (ct.getKind() == VOID) {
			Token void_token = accept(VOID);			
			id = accept(IDENTIFIER);
			
			TypeDenoter void_type = new BaseType(TypeKind.VOID, void_token.getStart());
			return parseMethodContent(new FieldDecl(isPrivate, isStatic, void_type, id.getSpelling(), id.getStart()));
		} else {
			TypeDenoter type = parseType();
			id = accept(IDENTIFIER);
			
			FieldDecl field = new FieldDecl(isPrivate, isStatic, type, id.getSpelling(), id.getStart());
			if (ct.getKind() == SEMICOLON) {
				acceptIt();
				return field;
			} else {
				return parseMethodContent(field);
			}
		}
	}
	
	MethodDecl parseMethodContent(FieldDecl field) throws SyntaxError {
		accept(PAREN_OPEN);
		
		ParameterDeclList pl;
		if (ct.getKind() == PAREN_CLOSE) {
			pl = new ParameterDeclList();
			acceptIt();
		} else {
			pl = parseParamList();
			accept(PAREN_CLOSE);
		}
		
		accept(CURL_OPEN);
		
		// While look ahead is in starters[[Statement]]
		StatementList sl = new StatementList();
		while (Arrays.binarySearch(STARTERS_STATEMENT, ct.getKind()) >= 0) {
			sl.add(parseStatement());
		}
		
		accept(CURL_CLOSE);
		
		return new MethodDecl(field, pl, sl, field.posn);
	}
	
	TypeDenoter parseType() throws SyntaxError {
		switch (ct.getKind()) {
		case IDENTIFIER:
			return parseIdType();
		case INT: case BOOLEAN:
			return parsePrimativeType();
		default:
			throw new SyntaxError(lexer.getSourceFile(), new TokenKind[] { INT, IDENTIFIER, BOOLEAN }, ct);
		}
	}
	
	TypeDenoter parsePrimativeType() throws SyntaxError {
		TypeDenoter type;
		switch (ct.getKind()) {
		case INT:
			Token int_token = acceptIt();
			type = new BaseType(TypeKind.INT, int_token.getStart());
			if (ct.getKind() == SQR_OPEN) {
				accept(SQR_OPEN);
				accept(SQR_CLOSE);
				type = new ArrayType(type, int_token.getStart());
			}
			break;
		case BOOLEAN:
			Token bool_token = acceptIt();
			type = new BaseType(TypeKind.BOOLEAN, bool_token.getStart());
			break;
		default:
			throw new SyntaxError(lexer.getSourceFile(), new TokenKind[] { INT, BOOLEAN }, ct);
		}
		
		return type;
	}
	
	TypeDenoter parseIdType() throws SyntaxError {
		Identifier id = new Identifier(accept(IDENTIFIER));
		TypeDenoter type = new ClassType(id, id.posn);
		if (ct.getKind() == SQR_OPEN) {
			accept(SQR_OPEN);
			accept(SQR_CLOSE);
			type = new ArrayType(type, type.posn);
		}
		
		return type;
	}
	
	ParameterDeclList parseParamList() throws SyntaxError {
		ParameterDeclList pl = new ParameterDeclList();
		
		TypeDenoter type = parseType();
		Token id = accept(IDENTIFIER);
		pl.add(new ParameterDecl(type, id.getSpelling(), id.getStart()));
		
		while (ct.getKind() == COMMA) {
			accept(COMMA);
			
			type = parseType();
			id = accept(IDENTIFIER);
			pl.add(new ParameterDecl(type, id.getSpelling(), id.getStart()));
		}
		
		return pl;
	}
	
	ExprList parseArgList() throws SyntaxError {
		ExprList list = new ExprList();
		
		list.add(parseExpression());
		while (ct.getKind() == COMMA) {
			accept(COMMA);
			list.add(parseExpression());
		}
		
		return list;
	}
	
	Reference parseReference() throws SyntaxError {
		BaseRef ref;
		if (ct.getKind() == THIS) {
			Token this_token = acceptIt();
			ref = new ThisRef(this_token.getStart());
		} else {
			Identifier id = new Identifier(accept(IDENTIFIER));
			if (ct.getKind() == SQR_OPEN) {
				Expression exp = parseIndexing();
				ref = new IxIdRef(id, exp, id.posn);
			} else {
				ref = new IdRef(id, id.posn);
			}
		}
		
		return parseReferenceExtension(ref);
	}	
	Reference parseReferenceExtension(Reference ref) throws SyntaxError {
		while(ct.getKind() == DOT) {
			acceptIt();
			Identifier id = new Identifier(accept(IDENTIFIER));
			if (ct.getKind() == SQR_OPEN) {
				Expression exp = parseIndexing();
				ref = new IxQRef(ref, id, exp, id.posn);
			} else {
				ref = new QRef(ref, id, id.posn);
			}
		}
		
		return ref;
	}
	
	Expression parseIndexing() throws SyntaxError {
		accept(SQR_OPEN);
		Expression exp = parseExpression();
		accept(SQR_CLOSE);
		
		return exp;
	}
	
	// Kinda helps with organization, overhead might not be worth it
	private static TokenKind[] STARTERS_STATEMENT = new TokenKind[] { 
			CURL_OPEN, RETURN, IF, WHILE, INT, BOOLEAN, IDENTIFIER, THIS 
		};
	static { Arrays.sort(STARTERS_STATEMENT); }
	
	Statement parseStatement() throws SyntaxError {
		Token start_token;
		switch (ct.getKind()) {
		case CURL_OPEN:
			start_token = acceptIt();
			StatementList list = new StatementList();
			while (Arrays.binarySearch(STARTERS_STATEMENT, ct.getKind()) >= 0)
				list.add(parseStatement());
			accept(CURL_CLOSE);
			
			return new BlockStmt(list, start_token.getStart());
		case RETURN:
			start_token = acceptIt();
			Expression return_exp = null;
			if (Arrays.binarySearch(STARTERS_EXPRESSION, ct.getKind()) >= 0)
				return_exp = parseExpression();
			accept(SEMICOLON);
			
			return new ReturnStmt(return_exp, start_token.getStart());
		case IF:
			start_token = acceptIt();
			accept(PAREN_OPEN);
			Expression if_cond = parseExpression();
			accept(PAREN_CLOSE);
			Statement if_then = parseStatement();
			Statement if_else = null;
			if (ct.getKind() == ELSE) {
				acceptIt();
				if_else = parseStatement();
			}
			
			return new IfStmt(if_cond, if_then, if_else, start_token.getStart());
		case WHILE:
			start_token = acceptIt();
			accept(PAREN_OPEN);
			Expression while_cond = parseExpression();
			accept(PAREN_CLOSE);
			Statement while_body = parseStatement();
			
			return new WhileStmt(while_cond, while_body, start_token.getStart());
		case INT: case BOOLEAN:
			TypeDenoter prim_type = parsePrimativeType();
			Token prim_var_id = accept(IDENTIFIER);
			Expression prim_var_exp = parseAssign();
			accept(SEMICOLON);
			
			return new VarDeclStmt(new VarDecl(prim_type, prim_var_id.getSpelling(), prim_type.posn), prim_var_exp, prim_type.posn);
		case IDENTIFIER:
			Statement id_stmt = null;
			Identifier id1 = new Identifier(accept(IDENTIFIER));
			if (ct.getKind() == SQR_OPEN) {
				// Array. Could still either be: TypeId id Define | Reference ( Assign | Invoke )
				accept(SQR_OPEN);
				if (Arrays.binarySearch(STARTERS_EXPRESSION, ct.getKind()) >= 0) {
					// If there is an Expression in square brackets, it's: Reference ( Assign | Invoke )
					Expression ix_expr = parseExpression();
					accept(SQR_CLOSE);
					
					id_stmt = parseReferenceStatement(new IxIdRef(id1, ix_expr, id1.posn));
				} else {
					// Otherwise just an array type: TypeId id Define
					accept(SQR_CLOSE);
					Token id2 = accept(IDENTIFIER);
					
					Expression value_expr = parseAssign();
					id_stmt = new VarDeclStmt(new VarDecl(new ArrayType(new ClassType(id1, id1.posn), id1.posn), id2.getSpelling(), id1.posn), value_expr, id1.posn);
				}
			} else if (ct.getKind() == IDENTIFIER) {
				// Got "id id", must be: IdType id Assign
				Token id2 = accept(IDENTIFIER);
				Expression value_expr = parseAssign();
				id_stmt = new VarDeclStmt(new VarDecl(new ClassType(id1, id1.posn), id2.getSpelling(), id1.posn), value_expr, id1.posn);
			} else {
				// Only other thing it could be is a Reference
				id_stmt = parseReferenceStatement(new IdRef(id1, id1.posn));
			}
			accept(SEMICOLON);
			
			return id_stmt;
		case THIS: // TODO We are modifying this - allowed in original grammar, but seems illegal?
			Token this_token = accept(THIS);
			id_stmt = parseReferenceStatement(new ThisRef(this_token.getStart()));
			accept(SEMICOLON);
			
			return id_stmt;
		default:
			throw new SyntaxError(lexer.getSourceFile(), STARTERS_STATEMENT, ct);
		}
	}
	
	Statement parseReferenceStatement(BaseRef base_ref) throws SyntaxError {
		Reference ref = parseReferenceExtension(base_ref);
		
		// Reference ( Assign | Invoke ) 
		if (ct.getKind() == ASSIGN) {
			Expression expr = parseAssign();
			return new AssignStmt(ref, expr, ref.posn);
		} else if (ct.getKind() == PAREN_OPEN) {
			ExprList args = parseInvoke();
			return new CallStmt(ref, args, ref.posn);
		} else {
			throw new SyntaxError(lexer.getSourceFile(), new TokenKind[] { DOT, ASSIGN, PAREN_OPEN }, ct);
		}
	}
	
	Expression parseAssign() throws SyntaxError {
		accept(ASSIGN);
		return parseExpression();
	}
	
	ExprList parseInvoke() throws SyntaxError {
		accept(PAREN_OPEN);

		ExprList args = new ExprList();
		if (Arrays.binarySearch(STARTERS_EXPRESSION, ct.getKind()) >= 0)
			args = parseArgList();
		
		accept(PAREN_CLOSE);
		
		return args;
	}
	
	// Kinda helps with organization, overhead might not be worth it
	private static TokenKind[] STARTERS_EXPRESSION = new TokenKind[] { 
			PAREN_OPEN, NUM, TRUE, FALSE, NULL, NEW, 
			NOT, MINUS, // unop 
			THIS, IDENTIFIER // STARTERS_REFERENCE
		};
	static { Arrays.sort(STARTERS_EXPRESSION); }
	
	Expression parseExpression() throws SyntaxError {
		return parseDisjunction();
	}
	
	Expression parseDisjunction() throws SyntaxError {
		Expression res = parseConjunction();
		while (ct.getKind() == TokenKind.OR) {
			Operator op = new Operator(acceptIt());
			res = new BinaryExpr(op, res, parseConjunction(), res.posn);
		}
		
		return res;
	}
	
	Expression parseConjunction() throws SyntaxError {
		Expression res = parseEquality();
		while (ct.getKind() == TokenKind.AND) {
			Operator op = new Operator(acceptIt());
			res = new BinaryExpr(op, res, parseEquality(), res.posn);
		}

		return res;
	}
	
	Expression parseEquality() throws SyntaxError {
		Expression res = parseRelational();
		while (ct.getKind() == TokenKind.EQU || ct.getKind() == TokenKind.NOT_EQU) {
			Operator op = new Operator(acceptIt());
			res = new BinaryExpr(op, res, parseRelational(), res.posn);
		}

		return res;
	}
	
	Expression parseRelational() throws SyntaxError {
		Expression res = parseAdditive();
		while (ct.getKind() == TokenKind.LSS || ct.getKind() == TokenKind.LSS_EQU
				|| ct.getKind() == TokenKind.GTR || ct.getKind() == TokenKind.GTR_EQU) {
			Operator op = new Operator(acceptIt());
			res = new BinaryExpr(op, res, parseAdditive(), res.posn);
		}

		return res;
	}
	
	Expression parseAdditive() throws SyntaxError {
		Expression res = parseMultiplicative();
		while (ct.getKind() == TokenKind.PLUS || ct.getKind() == TokenKind.MINUS) {
			Operator op = new Operator(acceptIt());
			res = new BinaryExpr(op, res, parseMultiplicative(), res.posn);
		}

		return res;
	}
	
	Expression parseMultiplicative() throws SyntaxError {
		Expression res = parseValue();
		while (ct.getKind() == TokenKind.MULT || ct.getKind() == TokenKind.DIV) {
			Operator op = new Operator(acceptIt());
			res = new BinaryExpr(op, res, parseValue(), res.posn);
		}
		
		return res;
	}
	
	Expression parseValue() throws SyntaxError {
		Expression exp;
		switch (ct.getKind()) {
		case PAREN_OPEN:
			accept(PAREN_OPEN);
			exp = parseExpression();
			accept(PAREN_CLOSE);
			return exp;
		case NUM:
			Token int_lit = acceptIt();
			return new LiteralExpr(new IntLiteral(int_lit), int_lit.getStart());
		case NULL:
			Token null_lit = acceptIt();
			return new LiteralExpr(new NullLiteral(null_lit), null_lit.getStart());
		case TRUE: case FALSE:
			Token bool_lit = acceptIt();
			return new LiteralExpr(new BooleanLiteral(bool_lit), bool_lit.getStart());
		case NEW:
			Token first_token = acceptIt();
			if (ct.getKind() == IDENTIFIER) {
				// Object or object array
				Identifier type_id = new Identifier(accept(IDENTIFIER));
				ClassType type = new ClassType(type_id, type_id.posn);
				if (ct.getKind() == PAREN_OPEN) {
					accept(PAREN_OPEN);
					accept(PAREN_CLOSE);
					exp = new NewObjectExpr(type, first_token.getStart());
				} else {
					Expression ix_exp = parseIndexing();
					exp = new NewArrayExpr(type, ix_exp, first_token.getStart());
				}
			} else {
				// Primitive array
				Token int_token = accept(INT);
				
				BaseType int_arr_type = new BaseType(TypeKind.INT, int_token.getStart());
				Expression ix_exp = parseIndexing();
				exp = new NewArrayExpr(int_arr_type, ix_exp, first_token.getStart());
			}
			return exp;
		case NOT: case MINUS: // unop
			Operator op = new Operator(acceptIt());
			Expression value = parseValue();
			return new UnaryExpr(op, value, op.posn);
		default:
			Reference ref = parseReference();
			if (ct.getKind() == PAREN_OPEN) {
				// Function call
				ExprList exprs = parseInvoke();
				return new CallExpr(ref, exprs, ref.posn);
			} else {
				return new RefExpr(ref, ref.posn);
			}
		}
	}
}
