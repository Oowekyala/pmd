/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.javadoc.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;

abstract class AbstractJavadocNode extends AbstractNode implements JavadocNode {

    private final JavadocNodeId id;

    public AbstractJavadocNode(JavadocNodeId id) {
        super(id.ordinal());
        this.id = id;
    }

    @Override
    public JavadocNode jjtGetChild(int index) {
        return (JavadocNode) super.jjtGetChild(index);
    }

    @Override
    public String getXPathNodeName() {
        return id.getXPathNodeName();
    }

    @Override
    public int getBeginLine() {
        return firstToken.getBeginLine();
    }

    @Override
    public int getBeginColumn() {
        return firstToken.getBeginColumn();
    }

    @Override
    public int getEndLine() {
        return lastToken.getEndLine();
    }

    @Override
    public int getEndColumn() {
        return lastToken.getEndColumn();
    }

    public JavadocNodeId getId() {
        return id;
    }
}