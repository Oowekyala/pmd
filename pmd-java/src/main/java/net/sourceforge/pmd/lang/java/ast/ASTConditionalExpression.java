/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * Represents a conditional expression, aka ternary expression. This operation has
 * a greater precedence as {@linkplain ASTExpression assignment expressions},
 * and lower as {@link ASTConditionalOrExpression}.
 *
 * <pre class="grammar">
 *
 * ConditionalExpression ::= {@linkplain ASTConditionalOrExpression ConditionalOrExpression} "?"  {@linkplain ASTExpression Expression} ":" {@linkplain ASTConditionalExpression ConditionalExpression}
 *
 * </pre>
 */
public final class ASTConditionalExpression extends AbstractJavaExpr implements ASTExpression {


    ASTConditionalExpression(int id) {
        super(id);
    }

    ASTConditionalExpression(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the node that represents the guard of this conditional.
     * That is the expression before the '?'.
     */
    public Node getGuardExpressionNode() {
        return jjtGetChild(0);
    }


    /**
     * Returns the node that represents the expression that will be evaluated
     * if the guard evaluates to true.
     */
    public ASTExpression getTrueAlternative() {
        return (ASTExpression) jjtGetChild(1);
    }


    /**
     * Returns the node that represents the expression that will be evaluated
     * if the guard evaluates to false.
     */
    public Node getFalseAlternative() {
        return jjtGetChild(2);
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
