/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.CollectionUtil;


/**
 * Represents class and interface declarations. This is a {@linkplain Node#isFindBoundary() find boundary}
 * for tree traversal methods.
 *
 * <pre class="grammar">
 *
 * ClassOrInterfaceDeclaration ::= ClassModifier*
 *                                 ( "class" | "interface" )
 *                                 &lt;IDENTIFIER&gt;
 *                                 {@linkplain ASTTypeParameters TypeParameters}?
 *                                 {@linkplain ASTExtendsList ExtendsList}?
 *                                 {@linkplain ASTImplementsList ImplementsList}?
 *                                 {@linkplain ASTClassOrInterfaceBody ClassOrInterfaceBody}
 *
 *
 * ClassModifier ::=  "public" | "private"  | "protected"
 *                  | "final"  | "abstract" | "static" | "strictfp"
 *                  | {@linkplain ASTAnnotation Annotation}
 *
 * </pre>
 */
public final class ASTClassOrInterfaceDeclaration extends AbstractAnyTypeDeclaration {

    private boolean isLocal;
    private boolean isLocalComputed; // guard for lazy evaluation of isLocal()

    private boolean isInterface;

    ASTClassOrInterfaceDeclaration(int id) {
        super(id);
    }

    ASTClassOrInterfaceDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public boolean isFindBoundary() {
        return isNested() || isLocal();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean isPackagePrivate() {
        return super.isPackagePrivate() && !isLocal();
    }

    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Returns true if the class is declared inside a block other
     * than the body of another class, or the top level.
     */
    public boolean isLocal() {
        if (!isLocalComputed) {
            Node current = jjtGetParent();
            while (current != null) {
                if (current instanceof ASTAnyTypeDeclaration) {
                    isLocal = false;
                    break;
                } else if (current instanceof ASTMethodOrConstructorDeclaration
                    || current instanceof ASTInitializer) {
                    isLocal = true;
                    break;
                }
                current = current.jjtGetParent();
            }
            if (current == null) {
                isLocal = false;
            }
            isLocalComputed = true;
        }
        return isLocal;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    void setInterface() {
        this.isInterface = true;
    }

    @Override
    public TypeKind getTypeKind() {
        return isInterface() ? TypeKind.INTERFACE : TypeKind.CLASS;
    }


    @Override
    public List<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return getFirstChildOfType(ASTClassOrInterfaceBody.class)
            .findChildrenOfType(ASTAnyTypeBodyDeclaration.class);
    }


    /**
     * Returns the superclass type node if this node is a class
     * declaration and explicitly declares an {@code extends}
     * clause. Superinterfaces of an interface are not considered.
     *
     * <p>Returns {@code null} otherwise.
     */
    public ASTClassOrInterfaceType getSuperClassTypeNode() {
        if (isInterface()) {
            return null;
        }

        ASTExtendsList extendsList = getFirstChildOfType(ASTExtendsList.class);
        return extendsList == null ? null : extendsList.iterator().next();
    }


    /**
     * Returns the interfaces implemented by this class, or
     * extended by this interface. Returns an empty list if
     * none is specified.
     */
    public List<ASTClassOrInterfaceType> getSuperInterfacesTypeNodes() {

        Iterable<ASTClassOrInterfaceType> it = isInterface()
                                               ? getFirstChildOfType(ASTExtendsList.class)
                                               : getFirstChildOfType(ASTImplementsList.class);

        return it == null ? Collections.<ASTClassOrInterfaceType>emptyList() : CollectionUtil.toList(it.iterator());
    }


    @Override
    public NodeMetaModel<? extends JavaNode> metaModel() {
        return new NodeMetaModel<ASTClassOrInterfaceDeclaration>(ASTClassOrInterfaceDeclaration.class) {
            @Override
            protected void writeAttributes(ASTClassOrInterfaceDeclaration node, DataOutputStream out) throws IOException {
                super.writeAttributes(node, out);
                out.writeUTF(node.getImage());
                out.writeBoolean(node.isInterface);
            }

            @Override
            protected void readAttributes(ASTClassOrInterfaceDeclaration node, DataInputStream in) throws IOException {
                super.readAttributes(node, in);
                node.setImage(in.readUTF());
                if (in.readBoolean()) {
                    node.setInterface();
                }
            }
        };
    }

}
