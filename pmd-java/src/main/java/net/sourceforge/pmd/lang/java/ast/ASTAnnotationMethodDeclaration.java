/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @deprecated Represented directly by {@link ASTMethodDeclaration MethodDeclaration}.
 *     An annotation method is just {@link ASTMethodDeclaration MethodDeclaration} whose
 *     enclosing type is an annotation.
 */
@Deprecated
public final class ASTAnnotationMethodDeclaration extends AbstractJavaAccessNode {

    ASTAnnotationMethodDeclaration(int id) {
        super(id);
    }

    ASTAnnotationMethodDeclaration(JavaParser p, int id) {
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
