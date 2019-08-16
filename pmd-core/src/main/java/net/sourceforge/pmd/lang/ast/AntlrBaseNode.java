/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.stream.Stream;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.util.DataMap;

public class AntlrBaseNode extends ParserRuleContext implements AntlrNode {

    // TODO: what should we do with parent? how do we handle data flows in this scenario? it's ok to ignore
    // TODO: our parent data flow in case we don't have one?
    // protected Node parent;

    private DataFlowNode dataFlowNode;
    private final DataMap dataMap = new DataMap();

    /**
     * Constructor required by {@link ParserRuleContext}
     */
    @SuppressWarnings("unused")
    public AntlrBaseNode() {
        // Nothing to be done
    }

    /**
     * Constructor required by {@link ParserRuleContext}
     *
     * @param parent The parent
     * @param invokingStateNumber the invokingState defined by {@link org.antlr.v4.runtime.RuleContext} parent
     */
    @SuppressWarnings("unused")
    public AntlrBaseNode(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public Node jjtGetParent() {
        return (Node) parent; // TODO: review if all parents are Nodes
    }

    @Override
    public int getBeginLine() {
        return start.getLine(); // This goes from 1 to n
    }

    @Override
    public int getEndLine() {
        return stop.getLine(); // This goes from 1 to n
    }

    @Override
    public int getBeginColumn() {
        return start.getCharPositionInLine(); // This goes from 0 to (n - 1)
    }

    @Override
    public int getEndColumn() {
        return stop.getCharPositionInLine(); // This goes from 0 to (n - 1)
    }

    @Override
    public DataFlowNode getDataFlowNode() {
        return dataFlowNode;
    }

    @Override
    public void setDataFlowNode(final DataFlowNode dataFlowNode) {
        this.dataFlowNode = dataFlowNode;
    }

    @Override
    public Object getUserData() {
        return dataMap.get(AbstractNode.LEGACY_USER_DATA);
    }

    @Override
    public DataMap getData() {
        return dataMap;
    }

    @Override
    public void setUserData(final Object userData) {
        dataMap.put(AbstractNode.LEGACY_USER_DATA, userData);
    }

    @Override
    public Node jjtGetChild(final int index) {
        try {
            return (Node) childrenStream().skip(index).findFirst().orElse(null);
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("Accessing invalid Antlr node", e);
        }
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public int jjtGetNumChildren() {
        return (int) childrenStream().count();
    }
    
    private Stream<Node> childrenStream() {
        return children == null ? Stream.empty() : children.stream().filter(e -> e instanceof Node).map(e -> (Node) e);
    }

    // TODO: should we make it abstract due to the comment in AbstractNode ?
    @Override
    public String getXPathNodeName() {
        final String simpleName = getClass().getSimpleName();
        return simpleName.substring(0, simpleName.length() - "Context".length());
    }
}
