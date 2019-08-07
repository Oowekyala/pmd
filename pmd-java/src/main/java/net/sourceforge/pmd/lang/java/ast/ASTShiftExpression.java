/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a shift expression on a numeric value. This has a precedence greater than {@link
 * ASTRelationalExpression}, and lower than {@link ASTAdditiveExpression}.
 *
 * <pre class="grammar">
 *
 * ShiftExpression ::=  {@linkplain ASTShiftExpression ShiftExpression} ( ( "&lt;&lt;"  | "&gt;&gt;" | "&gt;&gt;&gt;" ) {@linkplain ASTAdditiveExpression AdditiveExpression} )+
 *
 * </pre>
 *
 * <p>Note that the children of this node are not necessarily {@link ASTAdditiveExpression} or {@link ASTShiftExpression},
 * rather, they are expressions with an operator precedence greater or equal to those.
 */
public final class ASTShiftExpression extends AbstractLrBinaryExpr implements ASTExpression {
    ASTShiftExpression(int id) {
        super(id);
    }

    ASTShiftExpression(JavaParser p, int id) {
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
