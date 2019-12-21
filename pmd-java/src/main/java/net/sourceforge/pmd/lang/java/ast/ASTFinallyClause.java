/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * The "finally" clause of a {@linkplain ASTTryStatement try statement}.
 *
 *
 * <pre class="grammar">
 *
 * FinallyClause ::= "finally" {@link ASTBlock Block}
 *
 * </pre>
 */
public final class ASTFinallyClause extends AbstractJavaNode {

    ASTFinallyClause(int id) {
        super(id);
    }

    ASTFinallyClause(JavaParser p, int id) {
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
     * Returns the block.
     */
    public ASTBlock getBlock() {
        return (ASTBlock) jjtGetChild(0);
    }
}
