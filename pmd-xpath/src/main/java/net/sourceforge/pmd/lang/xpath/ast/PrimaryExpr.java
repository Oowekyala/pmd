/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;

/**
 * Marker for primary expressions. Primary expressions have the highest
 * precedence.
 *
 * <pre>
 *
 * PrimaryExpr ::= {@linkplain ASTNumericLiteral NumericLiteral}
 *               | {@linkplain ASTStringLiteral StringLiteral}
 *               | {@linkplain ASTVarRef VarRef}
 *               | {@linkplain ASTContextItemExpr ContextItemExpr}
 *               | {@linkplain ASTFunctionCall FunctionCall}
 *               | {@link FunctionItemExpr} )
 *
 * </pre>
 */
public interface PrimaryExpr extends Expr, StepExpr {
}
