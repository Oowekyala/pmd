/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A type pattern (JDK16). This can be found on
 * the right-hand side of an {@link ASTInfixExpression InstanceOfExpression},
 * in a {@link ASTPatternExpression PatternExpression}.
 *
 * <pre class="grammar">
 *
 * TypePattern ::= ( "final" | {@linkplain ASTAnnotation Annotation} )* {@linkplain ASTType Type} {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.java.net/jeps/394">JEP 394: Pattern Matching for instanceof</a>
*/
public final class ASTTypePattern extends AbstractJavaNode implements ASTPattern, AccessNode {

    ASTTypePattern(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the type against which the expression is tested.
     */
    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }

    /** Returns the declared variable. */
    public ASTVariableDeclaratorId getVarId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }
}
