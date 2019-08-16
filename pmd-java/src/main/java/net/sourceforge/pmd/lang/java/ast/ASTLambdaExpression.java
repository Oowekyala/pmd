/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A lambda expression.
 *
 *
 * <pre class="grammar">
 *
 * LambdaExpression ::= {@link ASTLambdaParameterList LambdaParameterList} "->" ( {@link ASTExpression Expression} | {@link ASTBlock Block} )
 *
 * </pre>
 */
public final class ASTLambdaExpression extends AbstractJavaExpr implements ASTExpression {

    ASTLambdaExpression(int id) {
        super(id);
    }


    ASTLambdaExpression(JavaParser p, int id) {
        super(p, id);
    }


    public ASTLambdaParameterList getParameters() {
        return (ASTLambdaParameterList) jjtGetChild(0);
    }


    public boolean isBlockBody() {
        return jjtGetChild(1) instanceof ASTBlock;
    }

    public boolean isExpressionBody() {
        return !isBlockBody();
    }


    @Override
    public boolean isFindBoundary() {
        return true;
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

}
