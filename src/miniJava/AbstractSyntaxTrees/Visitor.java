/**
 * miniJava public abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

/**
 * An implementation of the Visitor interface provides a method visitX
 * for each non-public abstract AST class X.  
 */
public abstract class Visitor {

  // Package
	public abstract Object visitPackage(Package prog, Object arg);

  // Declarations
    public abstract Object visitClassDecl(ClassDecl cd, Object arg);
    public abstract Object visitFieldDecl(FieldDecl fd, Object arg);
    public abstract Object visitMethodDecl(MethodDecl md, Object arg);
    public abstract Object visitParameterDecl(ParameterDecl pd, Object arg);
    public abstract Object visitVarDecl(VarDecl decl, Object arg);
    public abstract Object visitThisDecl(ThisDecl decl, Object arg);
    public abstract Object visitArrayIdxDecl(ArrayIdxDecl decl, Object arg);
    public abstract Object visitStringLiteralDecl(StringLiteralDecl sld, Object o);
 
  // Types
    public abstract Object visitBaseType(BaseType type, Object arg);
    public abstract Object visitClassType(ClassType type, Object arg);
    public abstract Object visitArrayType(ArrayType type, Object arg);
    
  // Statements
    public abstract Object visitBlockStmt(BlockStmt stmt, Object arg);
    public abstract Object visitVardeclStmt(VarDeclStmt stmt, Object arg);
    public abstract Object visitAssignStmt(AssignStmt stmt, Object arg);
    public abstract Object visitCallStmt(CallStmt stmt, Object arg);
    public abstract Object visitReturnStmt(ReturnStmt stmt, Object arg);
    public abstract Object visitIfStmt(IfStmt stmt, Object arg);
    public abstract Object visitWhileStmt(WhileStmt stmt, Object arg);
    
  // Expressions
    public abstract Object visitUnaryExpr(UnaryExpr expr, Object arg);
    public abstract Object visitBinaryExpr(BinaryExpr expr, Object arg);
    public abstract Object visitRefExpr(RefExpr expr, Object arg);
    public abstract Object visitCallExpr(CallExpr expr, Object arg);
    public abstract Object visitLiteralExpr(LiteralExpr expr, Object arg);
    public abstract Object visitNewObjectExpr(NewObjectExpr expr, Object arg);
    public abstract Object visitNewArrayExpr(NewArrayExpr expr, Object arg);
    
  // References
    public abstract Object visitThisRef(ThisRef ref, Object arg);
    public abstract Object visitIdRef(IdRef ref, Object arg);
    public abstract Object visitIxIdRef(IxIdRef ref, Object arg);
    public abstract Object visitQRef(QRef ref, Object arg);
    public abstract Object visitIxQRef(IxQRef ref, Object arg);

  // Terminals
    public abstract Object visitIdentifier(Identifier id, Object arg);
    public abstract Object visitOperator(Operator op, Object arg);
    public abstract Object visitIntLiteral(IntLiteral num, Object arg);
    public abstract Object visitBooleanLiteral(BooleanLiteral bool, Object arg);
    public abstract Object visitNullLiteral(NullLiteral nlit, Object arg);
    public abstract Object visitStringLiteral(StringLiteral slit, Object arg);
}
