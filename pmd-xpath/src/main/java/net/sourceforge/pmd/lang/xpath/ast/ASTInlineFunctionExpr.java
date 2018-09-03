/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;


/**
 * Inline function expression. An inline function expression creates an anonymous function
 * defined directly in the inline function expression itself. An inline function expression
 * specifies the names and SequenceTypes of the parameters to the function, the SequenceType
 * of the result, and the body of the function.
 *
 * <p>If a function parameter is declared using a name but no type, its default type is item()*.
 * If the result type is omitted from an inline function expression, its default result type is item()*.
 *
 * <p>The parameters of an inline function expression are considered to be variables whose scope
 * is the function body. It is a static error for an inline function expression to have more than
 * one parameter with the same name. Function parameter names can mask variables that would otherwise
 * be in scope for the function body.
 *
 * <pre>
 *
 * InlineFunctionExpr ::= "function" {@linkplain ASTParamList ParamList} ("as" {@linkplain ASTSequenceType SequenceType})? "{" {@link Expr} "}"
 *
 * </pre>
 */
public final class ASTInlineFunctionExpr extends AbstractXPathNode {


    ASTInlineFunctionExpr(XPathParser p, int id) {
        super(p, id);
    }


    @Override
    public <T> T jjtAccept(XPathParserVisitor<T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=b64c06535567c75890f8423e25c97e32 (do not edit this line) */
