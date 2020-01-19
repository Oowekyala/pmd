/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;

/**
 * Parenthesized expression. The parentheses bind more tightly than any other expression
 * (this is one of the primary expressions).
 *
 * <pre>
 *
 * ParenthesizedExpr ::= "(" {@link Expr} ")"
 *
 * </pre>
 */
public final class ASTParenthesizedExpr extends AbstractXPathNode implements PrimaryExpr, ParenthesizedNode<Expr> {

    /**
     * Constructor for synthetic node.
     *
     * @param wrapped Node wrapped in the parentheses
     */
    public ASTParenthesizedExpr(Expr wrapped) {
        super(XPathParserImplTreeConstants.JJTPARENTHESIZEDEXPR);
        insertSyntheticChild(wrapped, 0);
    }


    /**
     * Gets the expression wrapped in the parentheses.
     */
    @Override
    public Expr getWrappedNode() {
        return (Expr) getChild(0);
    }


    @Override
    public <T> void jjtAccept(XPathSideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    @Override
    public <R, T> R jjtAccept(XPathVisitor<R, T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
