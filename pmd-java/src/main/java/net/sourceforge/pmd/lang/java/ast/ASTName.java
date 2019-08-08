/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

public class ASTName extends AbstractJavaTypeNode {

    private NameDeclaration nd;


    /**
     * Constructor for a synthetic node.
     * @param image Image of the new node
     */
    @InternalApi
    @Deprecated
    public ASTName(String image) {
        super(JavaParserTreeConstants.JJTNAME);
        setImage(image);
    }

    ASTName(int id) {
        super(id);
    }

    ASTName(JavaParser p, int id) {
        super(p, id);
    }

    @InternalApi
    @Deprecated
    public void setNameDeclaration(NameDeclaration nd) {
        this.nd = nd;
    }

    public NameDeclaration getNameDeclaration() {
        return this.nd;
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
    public NodeMetaModel<? extends JavaNode> metaModel() {
        return NodeMetaModel.neverNullImage(getClass());
    }
}
