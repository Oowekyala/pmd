/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;


import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * Postfix expression.
 *
 * <pre>
 *
 * PostfixExpr ::= {@link PrimaryExpr} ({@link ASTPredicate Predicate} | {@link ASTArgumentList ArgumentList})+
 *
 * </pre>
 */
public final class ASTPostfixExpr extends AbstractXPathExpr implements Expr, StepExpr {

    /** Constructor for synthetic node. */
    public ASTPostfixExpr() {
        super(XPathParserImplTreeConstants.JJTPOSTFIXEXPR);
    }


    @Override
    public <T> void jjtAccept(XPathSideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    @Override
    public <R, T> R jjtAccept(XPathVisitor<R, T> visitor, T data) {
        return visitor.visit(this, data);
    }


    @Override
    public NodeStream<ASTPredicate> getPredicates() {
        return children(ASTPredicate.class);
    }
}
