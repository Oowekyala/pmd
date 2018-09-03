/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;

/**
 * Intersect or except expression.
 *
 * <pre>
 *
 * IntersectExceptExpr ::= {@linkplain ASTInstanceofExpr InstanceofExpr} ({@linkplain ASTIntersectExceptOperator IntersectExceptOperator} {@linkplain ASTInstanceofExpr InstanceofExpr})+
 *
 * </pre>
 *
 *
 */
public final class ASTIntersectExceptExpr extends AbstractXPathNode implements Expr {


    ASTIntersectExceptExpr(XPathParser p, int id) {
        super(p, id);
    }


    @Override
    public <T> T jjtAccept(XPathParserVisitor<T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=57fe2367422919138659f8fc5b8715b7 (do not edit this line) */
