/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a comparison on two numeric values. This has a precedence greater than {@link ASTEqualityExpression},
 * and lower than {@link ASTShiftExpression}. This has the same precedence as a {@link ASTInstanceOfExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTShiftExpression},
 * rather, they are expressions with an operator precedence greater or equal to ShiftExpression.
 *
 *
 * <pre class="grammar">
 *
 * RelationalExpression ::=  {@linkplain ASTShiftExpression ShiftExpression} ( ( "&lt;" | "&gt;" | "&lt;=" | "&gt;=" ) {@linkplain ASTShiftExpression ShiftExpression} )+
 *
 * </pre>
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public final class ASTRelationalExpression extends AbstractJavaExpr implements ASTExpression {

    ASTRelationalExpression(int id) {
        super(id);
    }

    ASTRelationalExpression(JavaParser p, int id) {
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
