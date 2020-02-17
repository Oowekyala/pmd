/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
public abstract class AbstractXPathNode extends AbstractNode implements XPathNode {

    protected final XPathParser parser;


    protected AbstractXPathNode(XPathParser parser, int id) {
        super(id);
        this.parser = parser;
    }


    @Override
    public final <T> T childrenAccept(XPathParserVisitor<T> visitor, T data) {
        if (children != null) {
            for (Node child : children) {
                ((XPathNode) child).jjtAccept(visitor, data);
            }
        }
        return data;
    }


    @Override
    public void jjtOpen() {
        if (beginLine == -1 && parser.token.next != null) {
            beginLine = parser.token.next.beginLine;
            beginColumn = parser.token.next.beginColumn;
        }
    }


    @Override
    public void jjtClose() {
        if (beginLine == -1 && (children == null || children.length == 0)) {
            beginColumn = parser.token.beginColumn;
        }
        if (beginLine == -1) {
            beginLine = parser.token.beginLine;
        }
        endLine = parser.token.endLine;
        endColumn = parser.token.endColumn;
    }


    void insertChild(Node child, int index) {
        // Allow to insert a child at random insert without overwriting
        if (children != null && index < children.length) {
            Node[] newChildren = new Node[children.length + 1];

            // toShift nodes are to the right of the insertion index
            int toShift = children.length - index;

            // copy the nodes before
            System.arraycopy(children, 0, newChildren, 0, index);

            // copy the nodes after
            System.arraycopy(children, index, newChildren, index + 1, toShift);
            children = newChildren;
        }
        super.jjtAddChild(child, index);
        child.jjtSetParent(this);
    }


    @Override
    public String getXPathNodeName() {
        return XPathParserTreeConstants.jjtNodeName[id];
    }
}
