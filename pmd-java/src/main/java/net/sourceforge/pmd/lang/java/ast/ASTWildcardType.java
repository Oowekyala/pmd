/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a wildcard type. Those can only occur when nested in an
 * {@link ASTTypeArguments} node.
 *
 * <pre class="grammar">
 *
 * WildcardType ::= "?" ( ("extends" | "super") {@link ASTReferenceType ReferenceType} )?
 *
 * </pre>
 */
public final class ASTWildcardType extends AbstractJavaTypeNode implements ASTReferenceType, LeftRecursiveNode {

    private boolean isUpperBound;

    ASTWildcardType(int id) {
        super(id);
    }


    ASTWildcardType(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public String getTypeImage() {
        return "?"; // TODO?
    }


    void setUpperBound(boolean upperBound) {
        isUpperBound = upperBound;
    }

    @Override
    public ASTTypeArguments jjtGetParent() {
        return (ASTTypeArguments) super.jjtGetParent();
    }

    /**
     * Returns true if this is an upper type bound, e.g.
     * in {@code <? extends Integer>}.
     */
    public boolean hasUpperBound() {
        return isUpperBound && jjtGetNumChildren() > 0;
    }


    /**
     * Returns true if this is a lower type bound, e.g.
     * in {@code <? super Node>}.
     */
    public boolean hasLowerBound() {
        return !isUpperBound && jjtGetNumChildren() > 0;
    }


    /**
     * Returns the type node representing the bound, e.g.
     * the {@code Node} in {@code <? super Node>}, or null.
     */
    @Nullable
    public ASTReferenceType getTypeBoundNode() {
        return getFirstChildOfType(ASTReferenceType.class);
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
