/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a type test on an object. This has a precedence greater than {@link ASTEqualityExpression},
 * and lower than {@link ASTShiftExpression}. This has the same precedence as a {@link ASTRelationalExpression}.
 *
 * <pre class="grammar">
 *
 * InstanceOfExpression ::=  {@linkplain ASTRelationalExpression RelationalExpression} "instanceof" {@linkplain ASTType Type}
 *
 * </pre>
 */
public final class ASTInstanceOfExpression extends AbstractJavaExpr implements ASTExpression {

    ASTInstanceOfExpression(int id) {
        super(id);
    }


    ASTInstanceOfExpression(JavaParser p, int id) {
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


    /**
     * Gets the type against which the expression is tested.
     */
    public ASTType getTypeNode() {
        return (ASTType) jjtGetChild(1);
    }

}
