/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a {@code for} loop (distinct from {@linkplain ASTForeachStatement foreach loops}).
 *
 * <pre class="grammar">
 *
 * ForStatement ::= "for" "(" {@linkplain ASTForInit ForInit}? ";" {@linkplain ASTExpression Expression}? ";" {@linkplain ASTForUpdate ForUpdate}? ")"
 *                      {@linkplain ASTStatement Statement}
 *
 * </pre>
 */
public final class ASTForStatement extends AbstractStatement {

    ASTForStatement(int id) {
        super(id);
    }


    ASTForStatement(JavaParser p, int id) {
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
     * Returns the node that represents the condition of this loop.
     * This may be any expression of type boolean.
     *
     * <p>If there is no specified guard, then returns null.
     */
    @Nullable
    public ASTExpression getCondition() {
        return getFirstChildOfType(ASTExpression.class);
    }

    /**
     * Returns the statement nested within the {@linkplain ASTForInit init clause}, if it exists.
     * This is either a {@linkplain ASTLocalVariableDeclaration local variable declaration} or a
     * {@linkplain ASTStatementExpressionList statement expression list}.
     */
    public @Nullable ASTStatement getInit() {
        ASTForInit init = AstImplUtil.getChildAs(this, 0, ASTForInit.class);
        return init == null ? null : init.getStatement();
    }

    /**
     * Returns the statement nested within the update clause, if it exists.
     */
    public @Nullable ASTStatementExpressionList getUpdate() {
        ASTForUpdate update = getFirstChildOfType(ASTForUpdate.class);
        return update == null ? null : update.getExprList();
    }

    /** Returns the statement that represents the body of this loop. */
    public ASTStatement getBody() {
        return (ASTStatement) jjtGetChild(jjtGetNumChildren() - 1);
    }

}
