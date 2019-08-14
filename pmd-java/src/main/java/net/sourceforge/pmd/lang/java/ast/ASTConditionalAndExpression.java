/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a boolean AND-expression. This has a precedence greater than {@link ASTConditionalOrExpression},
 * and lower than {@link ASTInclusiveOrExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTInclusiveOrExpression},
 * rather, they are expressions with an operator precedence greater or equal to InclusiveOrExpression.
 *
 *
 * <pre class="grammar">
 *
 * ConditionalAndExpression ::=  {@linkplain ASTInclusiveOrExpression InclusiveOrExpression} ( "&amp;&amp;" {@linkplain ASTInclusiveOrExpression InclusiveOrExpression} )+
 *
 * </pre>
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public final class ASTConditionalAndExpression extends AbstractJavaExpr implements ASTExpression {
    ASTConditionalAndExpression(int id) {
        super(id);
    }


    ASTConditionalAndExpression(JavaParser p, int id) {
        super(p, id);
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
