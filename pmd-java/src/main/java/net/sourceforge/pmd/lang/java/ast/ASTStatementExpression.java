/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @deprecated Use {@link ASTExpression} inside {@link ASTStatementExpressionList},
 *     or {@link ASTExpressionStatement} inside {@link ASTBlock}
 */
@Deprecated
public final class ASTStatementExpression extends AbstractJavaTypeNode {

    ASTStatementExpression(int id) {
        super(id);
    }

    ASTStatementExpression(JavaParser p, int id) {
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
