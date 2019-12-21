/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

/**
 * A list of statement expressions. Statement expressions are those
 * expressions which can appear in an {@linkplain ASTExpressionStatement expression statement}.
 *
 *
 * <p>Statement expression lists occur only {@link ASTForInit} and {@link ASTForUpdate}.
 * To improve the API of {@link ASTForInit}, however, this node implements {@link ASTStatement}.
 *
 * <pre class="grammar">
 *
 * StatementExpressionList ::= {@link ASTExpression Expression} ( "," {@link ASTExpression Expression} )*
 *
 * </pre>
 */
public final class ASTStatementExpressionList extends AbstractStatement implements Iterable<ASTExpression> {

    ASTStatementExpressionList(int id) {
        super(id);
    }

    ASTStatementExpressionList(JavaParser p, int id) {
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

    @Override
    public Iterator<ASTExpression> iterator() {
        return children(ASTExpression.class).iterator();
    }
}
