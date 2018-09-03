/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;


/**
 * A predicate occurring in a {@linkplain StepExpr StepExpr}.
 *
 * <pre>
 *
 * Predicate ::= "[" {@link Expr} "]"
 *
 * </pre>
 */
public final class ASTPredicate extends AbstractXPathNode {


    ASTPredicate(XPathParser p, int id) {
        super(p, id);
    }


    /**
     * Gets the expression wrapped in the predicate.
     */
    public Expr getWrappedExpression() {
        return (Expr) jjtGetChild(0);
    }


    @Override
    public <T> T jjtAccept(XPathParserVisitor<T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=da66ad6f42ac28b3cf50f8457dd9033a (do not edit this line) */
