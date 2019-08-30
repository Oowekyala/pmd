/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a type parameter declaration of a method, constructor, class or interface declaration.
 *
 * <p>The bound of a type parameter may only be an upper bound ("extends").
 * The bound is represented by the type node directly. The type node may
 * be an {@link ASTIntersectionType intersection type}.
 *
 * <pre class="grammar">
 *
 * TypeParameter ::= {@link ASTAnnotation Annotation}* &lt;IDENTIFIER&gt; ( "extends" {@link ASTReferenceType Type} )?
 *
 * </pre>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-4.html#jls-4.4">JLS</a>
 */
public final class ASTTypeParameter extends AbstractJavaTypeNode implements Annotatable {

    ASTTypeParameter(int id) {
        super(id);
    }


    ASTTypeParameter(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the name of the type variable introduced by this declaration.
     */
    public String getParameterName() {
        return getImage();
    }


    /**
     * Returns true if this type parameter is bounded,
     * in which case {@link #getTypeBoundNode()} doesn't
     * return {@code null}.
     */
    public boolean hasTypeBound() {
        return getTypeBoundNode() != null;
    }


    /**
     * Returns the type bound node of this parameter,
     * or null if it is not bounded.
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
