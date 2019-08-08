/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * An array access expression.
 *
 * <pre class="grammar">
 *
 * ArrayAccess ::=  {@link ASTPrimaryExpression PrimaryExpression} "["  {@link ASTExpression Expression} "]"
 *
 * </pre>
 */
public final class ASTArrayAccess extends AbstractJavaExpr implements ASTAssignableExpr, LeftRecursiveNode {

    ASTArrayAccess(int id) {
        super(id);
    }


    ASTArrayAccess(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the expression to the left of the "[".
     */
    public ASTPrimaryExpression getLhsExpression() {
        return (ASTPrimaryExpression) jjtGetChild(0);
    }

    /**
     * Returns the expression within the brackets.
     */
    public ASTExpression getIndexExpression() {
        return (ASTPrimaryExpression) jjtGetChild(1);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    @Override
    public NodeMetaModel<? extends JavaNode> metaModel() {
        return new NodeMetaModel<>(getClass(), 2);
    }
}
