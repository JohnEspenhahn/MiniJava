/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

/**
 * An implementation of the Visitor interface provides a method visitX
 * for each non-abstract AST class X.  
 */
public class Visitor {

  // Package
	public Object visitPackage(Package prog, Object arg) { return null; }

  // Declarations
    public Object visitClassDecl(ClassDecl cd, Object arg) { return null; }
    public Object visitFieldDecl(FieldDecl fd, Object arg) { return null; }
    public Object visitMethodDecl(MethodDecl md, Object arg) { return null; }
    public Object visitParameterDecl(ParameterDecl pd, Object arg) { return null; }
    public Object visitVarDecl(VarDecl decl, Object arg) { return null; }
    public Object visitThisDecl(ThisDecl decl, Object arg) { return null; }
    public Object visitArrayIdxDecl(ArrayIdxDecl decl, Object arg) { return null; }
 
  // Types
    public Object visitBaseType(BaseType type, Object arg) { return null; }
    public Object visitClassType(ClassType type, Object arg) { return null; }
    public Object visitArrayType(ArrayType type, Object arg) { return null; }
    
  // Statements
    public Object visitBlockStmt(BlockStmt stmt, Object arg) { return null; }
    public Object visitVardeclStmt(VarDeclStmt stmt, Object arg) { return null; }
    public Object visitAssignStmt(AssignStmt stmt, Object arg) { return null; }
    public Object visitCallStmt(CallStmt stmt, Object arg) { return null; }
    public Object visitReturnStmt(ReturnStmt stmt, Object arg) { return null; }
    public Object visitIfStmt(IfStmt stmt, Object arg) { return null; }
    public Object visitWhileStmt(WhileStmt stmt, Object arg) { return null; }
    
  // Expressions
    public Object visitUnaryExpr(UnaryExpr expr, Object arg) { return null; }
    public Object visitBinaryExpr(BinaryExpr expr, Object arg) { return null; }
    public Object visitRefExpr(RefExpr expr, Object arg) { return null; }
    public Object visitCallExpr(CallExpr expr, Object arg) { return null; }
    public Object visitLiteralExpr(LiteralExpr expr, Object arg) { return null; }
    public Object visitNewObjectExpr(NewObjectExpr expr, Object arg) { return null; }
    public Object visitNewArrayExpr(NewArrayExpr expr, Object arg) { return null; }
    
  // References
    public Object visitThisRef(ThisRef ref, Object arg) { return null; }
    public Object visitIdRef(IdRef ref, Object arg) { return null; }
    public Object visitIxIdRef(IxIdRef ref, Object arg) { return null; }
    public Object visitQRef(QRef ref, Object arg) { return null; }
    public Object visitIxQRef(IxQRef ref, Object arg) { return null; }

  // Terminals
    public Object visitIdentifier(Identifier id, Object arg) { return null; }
    public Object visitOperator(Operator op, Object arg) { return null; }
    public Object visitIntLiteral(IntLiteral num, Object arg) { return null; }
    public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) { return null; }
    public Object visitNullLiteral(NullLiteral nlit, Object arg) { return null; }
}
